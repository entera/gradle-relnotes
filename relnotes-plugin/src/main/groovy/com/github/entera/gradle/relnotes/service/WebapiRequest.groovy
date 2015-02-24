package com.github.entera.gradle.relnotes.service

import groovy.transform.Canonical

@Canonical
class WebapiRequest {
    URL url
    Map<String, String> params
}
