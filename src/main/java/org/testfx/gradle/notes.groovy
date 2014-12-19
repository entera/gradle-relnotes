package org.testfx.gradle
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.annotation.Nullable
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Release {
    String tagName
    ZonedDateTime releasedAt
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class PullRequest {
    String number
    String title
    ZonedDateTime mergedAt
    Release refRelease
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Commit {
    String pullNumber
    @Nullable String authorLogin
    String authorName
    String message
    PullRequest refPullRequest
    Author refAuthor
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Author {
    @Nullable String login
    String name
}

class ReleaseNotes {

    List<Release> releases
    List<PullRequest> pulls
    List<Commit> commits
    List<Author> authors

    void assignReleasesToPullRequests() {
        def releases = releases.asImmutable()
            .sort(false) { rel -> rel.releasedAt }
        pulls.each { pull ->
            pull.refRelease = releases
                .find { rel -> pull.mergedAt <= rel.releasedAt }
        }
    }

    void assignPullRequestsToCommits() {
        Map<String, PullRequest> pullRefs = pulls
            .collectEntries { pull -> [pull.number, pull] }
        commits.each { commit ->
            commit.refPullRequest = pullRefs[commit.pullNumber]
        }
    }

    void assignCommitsToAuthors() {
        Map<String, Author> authorRefs = authors
            .collectEntries { author -> [author.login, author] }
        commits.each { commit ->
            commit.refAuthor = authorRefs[commit.authorLogin]
        }
    }
}

class ReleaseNotesPrinter {

    ReleaseNotes releaseNotes

    String generateReleaseNotes(Release release) {
        def output = new StringBuilder()
        output << generateReleaseHeader(release)
        output << "\n"
        output << generateAuthorList(release)
        output << "\n"
        output << generatePullList(release)
        return output.toString()
    }

    private String generateReleaseHeader(Release release) {
        def formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.ENGLISH)
        def tagNameText = release.tagName
        def releasedAtText = release.releasedAt.format(formatter)
        return "## ${tagNameText} (${releasedAtText})\n"
    }

    private String generateAuthorList(Release release) {
        def releaseCommits = releaseNotes.commits.asImmutable()
            .findAll { it.refPullRequest.refRelease == release }
        def releaseAuthors = releaseCommits.asImmutable()
            .unique(false) { it.refAuthor }
            .collect { it.refAuthor }
            .sort(false) { releaseCommits.findAll { it.refAuthor == it }.size() }
            .reverse()
        def numOfCommits = releaseCommits.size()
        def numOfAuthors = releaseCommits
            .unique(false) { it.authorName }.size()

        def output = new StringBuilder()
        output << "- ${plural(numOfCommits, "commit", "commits")} by " +
            "${plural(numOfAuthors, "author", "authors")}:\n"
        for (author in releaseAuthors) {
            def numOfAuthorCommits = releaseCommits
                .findAll { it.refAuthor == author }.size()
            output << "  - **${author.name}** @${author.login} " +
                "(${plural(numOfAuthorCommits, "commit", "commits")})\n"
        }
        return output.toString()
    }

    private String generatePullList(Release release) {
        def pullsInRelease = releaseNotes.pulls
            .findAll { it.refRelease == release }
            .sort(false) { it.title }
        def numOfPulls = pullsInRelease.size()

        def output = new StringBuilder()
        output << "- ${plural(numOfPulls, "merged pull request", "merged pull requests")}:\n"
        for (pull in pullsInRelease) {
            def numOfPullCommits = releaseNotes.commits
                .findAll { it.refPullRequest == pull }.size()
            output << "  - ${pull.title} #${pull.number} " +
                "(${plural(numOfPullCommits, "commit", "commits")})\n"

        }
        return output.toString()
    }

    private String plural(int count,
                          String singular,
                          String plural) {
        return "" + count + " " + (count == 1 ? singular : plural)
    }

}

//class ReleaseNotesTemplate {
//    String templateString = '''
//        ## <%= tagName %> (<%= releasedAt.format(longDateFormatter) %>)
//
//        - <%= numOfCommits %> commits by <%= numOfAuthors %> authors:
//        <% for (author in releaseAuthors) { %>\
//          - test
//        <% } %>
//    '''.stripIndent().trim()
//
//    String generateReleaseNotes(Release release) {
//        def templateEngine = new SimpleTemplateEngine()
//        def template = templateEngine.createTemplate(templateString)
//        println template.make([
//            tagName       : "1.0.0",
//            releasedAt    : ZonedDateTime.parse("2010-01-01T11:34:56Z"),
//            longDateFormatter: DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.ENGLISH),
//            numOfCommits  : 2,
//            numOfAuthors  : 3,
//            releaseAuthors: ["foo", "bar"]
//        ]).toString()
//    }
//}
