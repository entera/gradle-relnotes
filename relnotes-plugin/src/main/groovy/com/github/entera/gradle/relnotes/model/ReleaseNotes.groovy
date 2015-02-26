package com.github.entera.gradle.relnotes.model

class ReleaseNotes {
    List<Release> releases

    List<Commit> commits
    List<Author> authors

    List<PullRequest> pullRequests
    List<Commit> pullCommits
}
