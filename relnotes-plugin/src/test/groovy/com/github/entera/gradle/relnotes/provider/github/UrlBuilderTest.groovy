package com.github.entera.gradle.relnotes.provider.github

import org.hamcrest.Matchers
import org.junit.Test

import static org.junit.Assert.assertThat

class UrlBuilderTest {
    @Test
    void "#toUrl()"() {
        // given:
        def urlBuilder = UrlBuilder.build()

        // expect:
        urlBuilder.repo(":ownerRepo").resource(":entityType").params(":params")
        assertThat urlBuilder.toUrl(), Matchers.is(
            new URL("https://api.github.com/repos/:ownerRepo/:entityType?:params")
        )
    }

    @Test
    void "#page()"() {
        // given:
        def urlBuilder = UrlBuilder.build().repo(":ownerRepo").resource(":entityType")

        // expect:
        assertThat(urlBuilder.page(36).toUrl(), Matchers.is(
            new URL("https://api.github.com/repos/:ownerRepo/:entityType?page=36")
        ))

        // expect:
        assertThat urlBuilder.page(37).toUrl(), Matchers.is(
            new URL("https://api.github.com/repos/:ownerRepo/:entityType?page=37")
        )
    }

    @Test
    void "#resource()"() {
        // given:
        def urlBuilder = UrlBuilder.build().resource("users/:username")

        // expect:
        assertThat urlBuilder.toUrl(), Matchers.is(
            new URL("https://api.github.com/users/:username")
        )
    }
}
