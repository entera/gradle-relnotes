package com.github.entera.gradle.relnotes.adapter.github

class UrlBuilder {
    String repo
    String resource
    String params
    Integer page

    private UrlBuilder() {}

    static UrlBuilder build() {
        return new UrlBuilder()
    }

    UrlBuilder repo(String repo) {
        this.repo = repo; this
    }

    UrlBuilder resource(String resource) {
        this.resource = resource; this
    }

    UrlBuilder params(String params) {
        this.params = params; this
    }

    UrlBuilder page(Integer page) {
        this.page = page; this
    }

    URL toUrl() {
        def urlString = "https://api.github.com/"
        if (repo != null) {
            urlString += "repos/${repo}/"
        }
        urlString += "${resource}?"
        if (page != null) {
            urlString += "page=${page}&"
        }
        if (params != null) {
            urlString += "${params}&"
        }
        return urlString.minus(~/([\?&]+)$/).toURL()
    }
}
