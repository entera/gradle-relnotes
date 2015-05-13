package de.entera.gradle.relnotes.printer

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

import de.entera.gradle.relnotes.model.Author
import de.entera.gradle.relnotes.model.PullRequest
import de.entera.gradle.relnotes.model.ReleaseNotes
import de.entera.gradle.relnotes.model.Commit
import de.entera.gradle.relnotes.model.Release
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.parse
import static org.junit.Assert.assertThat

class ReleaseNotesPrinterTest {
    List<Release> releases
    List<PullRequest> pulls
    List<Commit> commits
    List<Author> authors

    ReleaseNotes releaseNotes
    ReleaseNotesPrinter printer

    final DateTimeFormatter formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.ENGLISH)

    final String printedNotes0 = """
        <!-- tag: v1.0.0 -->
        ## v1.0.0 &mdash; January 1, 2010

        3 commits by 1 author:
        - **Alice Henderson** (@alice) &mdash; 3 commits

        1 merged pull request:
        - **feat(Foo):** ... (#1) &mdash; 3 commits
    """.stripIndent().trim() + "\n"

    final String printedNotes1 = """
        <!-- tag: v1.0.1 -->
        ## v1.0.1 &mdash; March 23, 2012

        4 commits by 2 authors:
        - **Carol Sanders** (@carol), **Ted Henderson** (@ted) &mdash; 2 commits

        2 merged pull requests:
        - **docs(Baz):** ... (#12) &mdash; 2 commits
        - **refactor(Bar):** ... (#11) &mdash; 2 commits
    """.stripIndent().trim() + "\n"

    final String printedNotes2 = """
        <!-- tag: v1.0.2-SNAPSHOT -->
        ## v1.0.2-SNAPSHOT &mdash; ${now().format(formatter)}

        5 commits by 2 authors:
        - **Alice Henderson** (@alice) &mdash; 4 commits
        - **Bob Sanders** (@bob) &mdash; 1 commit

        4 merged pull requests:
        - **chore(Quux):** ... (#21) &mdash; 1 commit
        - **fix(Quux):** ... (#22) &mdash; 2 commits
        - **test:** ... (#23) &mdash; 1 commit
        - ... (#24) &mdash; 1 commit
    """.stripIndent().trim() + "\n"

    @Before
    void setup() {
        releases = [
            new Release(tagName: "v1.0.0", releasedAt: parse("2010-01-01T00:00Z")),
            new Release(tagName: "v1.0.1", releasedAt: parse("2012-03-23T00:00Z")),
            new Release(tagName: "v1.0.2-SNAPSHOT", releasedAt: now()),
        ]
        pulls = [
            new PullRequest(number: "1", mergedAt: parse("2009-06-10T00:00Z"), title: "feat(Foo): ..."),

            new PullRequest(number: "11", mergedAt: parse("2011-06-10T00:00Z"), title: "refactor(Bar): ..."),
            new PullRequest(number: "12", mergedAt: parse("2011-06-10T00:00Z"), title: "docs(Baz): ..."),

            new PullRequest(number: "21", mergedAt: parse("2013-06-10T00:00Z"), title: "chore(Quux): ..."),
            new PullRequest(number: "22", mergedAt: parse("2013-06-10T00:00Z"), title: "fix(Quux): ..."),
            new PullRequest(number: "23", mergedAt: parse("2013-06-10T00:00Z"), title: "test: ..."),
            new PullRequest(number: "24", mergedAt: parse("2013-06-10T00:00Z"), title: "..."),
        ]
        commits = [
            new Commit(sha: "a1", pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(sha: "a2", pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(sha: "a3", pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),

            new Commit(sha: "b1", pullNumber: "11", authorLogin: "ted", authorName: "Ted Henderson"),
            new Commit(sha: "b2", pullNumber: "11", authorLogin: "ted", authorName: "Ted Henderson"),
            new Commit(sha: "b3", pullNumber: "12", authorLogin: "carol", authorName: "Carol Sanders"),
            new Commit(sha: "b4", pullNumber: "12", authorLogin: "carol", authorName: "Carol Sanders"),

            new Commit(sha: "c1", pullNumber: "21", authorLogin: "bob", authorName: "Bob Sanders"),
            new Commit(sha: "c2", pullNumber: "22", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(sha: "c3", pullNumber: "22", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(sha: "c4", pullNumber: "23", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(sha: "c5", pullNumber: "24", authorLogin: "alice", authorName: "Alice Henderson"),
        ]
        authors = [
            new Author(login: "alice", name: "Alice Henderson"),
            new Author(login: "bob", name: "Bob Sanders"),
            new Author(login: "carol", name: "Carol Sanders"),
            new Author(login: "ted", name: "Ted Henderson"),
        ]

        releaseNotes = new ReleaseNotes()
        releaseNotes.releases = releases
        releaseNotes.commits = commits
        releaseNotes.authors = authors
        releaseNotes.pullRequests = pulls
        releaseNotes.pullCommits = commits

        printer = new ReleaseNotesPrinter()
        printer.releaseNotes = releaseNotes
    }

    @Test
    void "should assign pull request merge to commits with no commits"() {
        // given:
        def releaseNotes = new ReleaseNotes()
        releaseNotes.commits = []
        releaseNotes.pullCommits = []

        // when:
        printer.releaseNotes = releaseNotes
        printer.assignPullRequestMergeToCommits()

        // then:
        assertThat releaseNotes.commits, Matchers.empty()
        assertThat releaseNotes.pullCommits, Matchers.empty()
    }

    @Test
    void "should assign pull request merge to commits with commits"() {
        // given:
        def releaseNotes = new ReleaseNotes()
        releaseNotes.commits = [
            new Commit(sha: "1", committedAt: parse("2010-01-01T00:00Z")),
            new Commit(sha: "2", committedAt: parse("2010-01-01T00:00Z")),
            new Commit(sha: "3", committedAt: parse("2010-01-01T00:00Z"))
        ]
        releaseNotes.pullCommits = [
            new Commit(sha: "1",
                refPullRequest: new PullRequest(mergedAt: parse("2010-10-12T00:00Z"))),
            new Commit(sha: "2",
                refPullRequest: new PullRequest(mergedAt: parse("2010-11-12T00:00Z"))),
            new Commit(sha: "3",
                refPullRequest: new PullRequest(mergedAt: parse("2010-12-12T00:00Z")))
        ]

        // when:
        printer.releaseNotes = releaseNotes
        printer.assignPullRequestMergeToCommits()

        // then:
        assertThat releaseNotes.commits, Matchers.contains(
            new Commit(sha: "1", committedAt: parse("2010-10-12T00:00Z")),
            new Commit(sha: "2", committedAt: parse("2010-11-12T00:00Z")),
            new Commit(sha: "3", committedAt: parse("2010-12-12T00:00Z"))
        )
    }

    @Test
    void "should assign releases to pull requests"() {
        // when:
        printer.assignReleasesToPullRequests()

        // then:
        assert pulls.find { it.number == "1" }.refRelease.tagName == "v1.0.0"
        assert pulls.find { it.number == "11" }.refRelease.tagName == "v1.0.1"
        assert pulls.find { it.number == "12" }.refRelease.tagName == "v1.0.1"
        assert pulls.find { it.number == "21" }.refRelease.tagName == "v1.0.2-SNAPSHOT"
        assert pulls.find { it.number == "22" }.refRelease.tagName == "v1.0.2-SNAPSHOT"
    }

    @Test
    void "should assign pull requests to commits"() {
        // when:
        printer.assignPullRequestsToPullCommits()

        // then:
        assert commits[0].refPullRequest == pulls[0]
        assert commits[1].refPullRequest == pulls[0]
        assert commits[2].refPullRequest == pulls[0]
        assert commits[3].refPullRequest == pulls[1]
        assert commits[4].refPullRequest == pulls[1]
        assert commits[5].refPullRequest == pulls[2]
        assert commits[5].refPullRequest == pulls[2]
    }

    @Test
    void "should assign commits to authors"() {
        // when:
        printer.assignCommitsToAuthors()

        // then:
        assert commits[0].refAuthor == authors[0]
        assert commits[1].refAuthor == authors[0]
        assert commits[2].refAuthor == authors[0]
        assert commits[3].refAuthor == authors[3]
        assert commits[4].refAuthor == authors[3]
        assert commits[5].refAuthor == authors[2]
        assert commits[6].refAuthor == authors[2]
    }

    @Test
    void "should generate release notes"() {
        // given:
        printer.assignReleasesToPullRequests()
        printer.assignPullRequestsToPullCommits()
        printer.assignPullRequestMergeToCommits()
        printer.assignReleasesToCommits()
        printer.assignCommitsToAuthors()

        // when:
        def generatedNotes0 = printer.generateReleaseNotes(releases[0])
        def generatedNotes1 = printer.generateReleaseNotes(releases[1])
        def generatedNotes2 = printer.generateReleaseNotes(releases[2])

        // then:
        assert generatedNotes0 == printedNotes0
        assert generatedNotes1 == printedNotes1
        assert generatedNotes2 == printedNotes2

        //releaseNotes.releases.reverse().each { rel ->
        //    println printer.generateReleaseNotes(rel)
        //}
    }

    @Test
    void "should format pull request title"() {
        // expect:
        assert formatPullRequestTitle("foo: bar") == "**foo:** bar"
        assert formatPullRequestTitle("foo bar: baz") == "foo bar: baz"
        assert formatPullRequestTitle("foo(bar): baz") == "**foo(bar):** baz"
        assert formatPullRequestTitle("foo(bar, baz): quux") == "**foo(bar, baz):** quux"
        assert formatPullRequestTitle("foo: bar\nbaz") == "**foo:** bar\nbaz"
    }

    private String formatPullRequestTitle(String title) {
        return title.replaceFirst(printer.PULL_TITLE_PATTERN, printer.PULL_TITLE_REPLACEMENT)
    }
}
