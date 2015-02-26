package com.github.entera.gradle.relnotes.notes

import com.github.entera.gradle.relnotes.notes.model.Commit
import com.github.entera.gradle.relnotes.notes.model.PullRequest
import org.hamcrest.Matchers
import org.junit.Test

import static java.time.ZonedDateTime.parse
import static org.junit.Assert.assertThat

class ReleaseNotesTest {

    @Test
    void "assign pull request merge to commits with no commits"() {
        // given:
        def releaseNotes = new ReleaseNotes()
        releaseNotes.commits = []
        releaseNotes.pullCommits = []

        // when:
        releaseNotes.assignPullRequestMergeToCommits()

        // then:
        assertThat releaseNotes.commits, Matchers.empty()
        assertThat releaseNotes.pullCommits, Matchers.empty()
    }

    @Test
    void "assign pull request merge to commits with commits"() {
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
        releaseNotes.assignPullRequestMergeToCommits()

        // then:
        assertThat releaseNotes.commits, Matchers.contains(
            new Commit(sha: "1", committedAt: parse("2010-10-12T00:00Z")),
            new Commit(sha: "2", committedAt: parse("2010-11-12T00:00Z")),
            new Commit(sha: "3", committedAt: parse("2010-12-12T00:00Z"))
        )
    }

}
