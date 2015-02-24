package com.github.entera.gradle.relnotes.service

import groovy.transform.Canonical

/**
 * Created by benjamin on 24.02.2015.
 */
@Canonical
class WebapiRequest {
    URL url
    Map<String, String> params
}
