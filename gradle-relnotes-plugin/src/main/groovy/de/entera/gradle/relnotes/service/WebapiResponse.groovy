package de.entera.gradle.relnotes.service

import groovy.transform.Canonical

@Canonical
class WebapiResponse {
    Object data
    Map<String, String> headers
}
