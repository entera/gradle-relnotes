package com.github.entera.gradle.relnotes.notes.model

import java.time.ZonedDateTime
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 * Created by benjamin on 24.02.2015.
 */
@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Issue {
    String number
    String title

    ZonedDateTime closedAt
    Release refRelease
}
