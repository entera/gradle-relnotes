package com.github.entera.gradle.relnotes.notes.model

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
