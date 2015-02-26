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
        def pullCommitsBySha = pullCommits.groupBy { it.sha }
        for (commit in commits) {
            def pullCommits = pullCommitsBySha[commit.sha]
            if (pullCommits) {
                def pullCommit = pullCommits.sort { it.refPullRequest.mergedAt }.first()
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
