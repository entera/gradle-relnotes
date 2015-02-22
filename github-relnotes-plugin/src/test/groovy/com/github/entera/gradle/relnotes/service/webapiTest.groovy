package com.github.entera.gradle.relnotes.service

import org.hamcrest.Matchers
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat

class WebapiClientTest {

    @Test
    void "json()"() {
        // given:
        def client = new WebapiClient()
        def request = new WebapiRequest(
            url: "https://api.github.com/rate_limit".toURL(),
            params: [
                "Accept": "application/json"
            ]
        )

        // when:
        def response = client.json(request)

        // then:
        assertThat(response.data, Matchers.notNullValue())
        assertThat(response.headers, Matchers.hasEntry("Status", "200 OK"))
    }

}
