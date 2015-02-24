package com.github.entera.gradle.relnotes.notes

import com.github.entera.gradle.relnotes.notes.model.Author
import com.github.entera.gradle.relnotes.notes.model.Commit
import com.github.entera.gradle.relnotes.notes.model.PullRequest
import com.github.entera.gradle.relnotes.notes.model.Release

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
