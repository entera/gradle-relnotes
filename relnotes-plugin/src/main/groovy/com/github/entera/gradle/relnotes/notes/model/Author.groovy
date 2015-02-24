package com.github.entera.gradle.relnotes.notes.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class Author {
    String name
    String email
    String login
}
