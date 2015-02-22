package org.testfx.gradle

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

    static final String PULL_TITLE_PATTERN = /(?s)^(\w+?:|\w+?\(.+?\):)(.+)/
    static final String PULL_TITLE_REPLACEMENT = "**\$1**\$2"

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
        def formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.ENGLISH)
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
        def releaseAuthorsMap = releaseAuthors.groupBy { author ->
            releaseCommits.findAll { it.refAuthor == author }.size()
        }

        def numOfCommits = releaseCommits.size()
        def numOfAuthors = releaseCommits
            .unique(false) { it.authorName }.size()

        def output = new StringBuilder()
        output << "${plural(numOfCommits, "commit", "commits")} by" +
            " ${plural(numOfAuthors, "author", "authors")}:\n"
        releaseAuthorsMap.each { int numOfAuthorCommits, List<Author> authors ->
            output << "- "
            output << authors.asImmutable()
                .sort(false) { it.name }
                .collect { author -> "**${author.name}** (@${author.login})"}
                .join(", ")
            output << " &mdash; ${plural(numOfAuthorCommits, "commit", "commits")}\n"
        }
        return output.toString()
    }

    private String generatePullList(Release release) {
        def pullsInRelease = releaseNotes.pulls
            .findAll { it.refRelease == release }
            .sort(false) { it.title }
            .sort(false) { !it.title.matches(PULL_TITLE_PATTERN) }
        def numOfPulls = pullsInRelease.size()

        def output = new StringBuilder()
        output << "${plural(numOfPulls, "merged pull request", "merged pull requests")}:\n"
        for (pull in pullsInRelease) {
            def pullTitle = pull.title.replaceFirst(PULL_TITLE_PATTERN, PULL_TITLE_REPLACEMENT)
            def numOfPullCommits = releaseNotes.commits
                .findAll { it.refPullRequest == pull }.size()
            output << "- ${pullTitle} (#${pull.number})"
            output << " &mdash; ${plural(numOfPullCommits, "commit", "commits")}\n"

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
