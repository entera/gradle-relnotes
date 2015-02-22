package org.testfx.gradle

import java.time.ZonedDateTime
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Release {
    String tagName
    String commitSha
    ZonedDateTime releasedAt
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class PullRequest {
    String number
    String title
    String baseRef
    ZonedDateTime mergedAt
    Release refRelease
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Commit {
    String sha
    String message
    String authorName
    String authorEmail
    String authorLogin
    String pullNumber
    ZonedDateTime committedAt

    Integer numOfParents
    Release refRelease
    PullRequest refPullRequest
    Author refAuthor
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Author {
    String name
    String email
    String login
}

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Issue {
    String number
    String title

    ZonedDateTime closedAt
    Release refRelease
}
