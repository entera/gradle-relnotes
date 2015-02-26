package com.github.entera.gradle.relnotes.notes

import com.github.entera.gradle.relnotes.notes.model.Author
import com.github.entera.gradle.relnotes.notes.model.Commit
import com.github.entera.gradle.relnotes.notes.model.PullRequest
import com.github.entera.gradle.relnotes.notes.model.Release

class ReleaseNotes {
    List<Release> releases

    List<Commit> commits
    List<Author> authors

    List<PullRequest> pullRequests
    List<Commit> pullCommits

    void assignPullRequestMergeToCommits() {
        // TODO: use datetime of first merged pull request (if available)
        for (commit in commits) {
            def pullCommit = pullCommits.find { it.sha == commit.sha }
            if (pullCommit) {
                commit.committedAt = pullCommit.refPullRequest.mergedAt
            }
        }
    }

    void assignReleasesToCommits() {
        def releases = releases.asImmutable()
            .sort(false) { rel -> rel.releasedAt }
        commits.each { commit ->
            commit.refRelease = releases
                .find { rel -> commit.committedAt <= rel.releasedAt }
        }
    }

    void assignCommitsToAuthors() {
        Map<String, Author> authorRefs = authors
            .collectEntries { author -> [author.login, author] }
        commits.each { commit ->
            commit.refAuthor = authorRefs[commit.authorLogin]
        }
    }

    void assignReleasesToPullRequests() {
        def releases = releases.asImmutable()
            .sort(false) { rel -> rel.releasedAt }
        pullRequests.each { pull ->
            pull.refRelease = releases
                .find { rel -> pull.mergedAt <= rel.releasedAt }
        }
    }

    void assignPullRequestsToPullCommits() {
        Map<String, PullRequest> pullRefs = pullRequests
            .collectEntries { pull -> [pull.number, pull] }
        pullCommits.each { commit ->
            commit.refPullRequest = pullRefs[commit.pullNumber]
        }
    }
}
