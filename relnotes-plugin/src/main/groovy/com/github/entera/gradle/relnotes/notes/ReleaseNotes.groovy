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
}
