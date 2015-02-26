package com.github.entera.gradle.relnotes.model

import java.time.ZonedDateTime
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Issue {
    String number
    String title

    ZonedDateTime closedAt
    Release refRelease
}
