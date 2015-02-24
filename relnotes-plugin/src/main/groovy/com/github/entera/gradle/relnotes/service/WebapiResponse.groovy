package com.github.entera.gradle.relnotes.service

import groovy.json.JsonSlurper
import groovy.transform.Canonical


@Canonical
class WebapiResponse {
    Object data
    Map<String, String> headers
}

