package com.github.entera.gradle.relnotes.notes.model

import java.time.ZonedDateTime
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Commit {
    String sha
    String message
    String authorName
    String authorEmail
    String authorLogin
    ZonedDateTime committedAt

    Integer numOfParents
    String pullNumber

    Release refRelease
    PullRequest refPullRequest
    Author refAuthor
}
