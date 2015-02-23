package com.github.entera.gradle.relnotes.provider.github

import groovy.json.JsonSlurper

import com.github.entera.gradle.relnotes.notes.Commit
import com.github.entera.gradle.relnotes.notes.PullRequest
import com.github.entera.gradle.relnotes.notes.Release
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

import static java.time.ZonedDateTime.parse
import static org.junit.Assert.assertThat

class DataReaderTest {

    DataReader dataReader

    @Before
    void setup() {
        dataReader = new DataReader()
    }

    @Test
    void "pulls"() {
        // given:
        def data = loadJsonFixture("res/github_response/pulls_list_pull_requests.json")

        // when:
        def pullRequests = dataReader.pulls(data)

        // then:
        assertThat(pullRequests, Matchers.contains(
            new PullRequest("1347", "new-feature", null, parse("2011-01-26T19:01:12Z"))
        ))
    }

    @Test
    void "pullCommits"() {
        // given:
        def data = loadJsonFixture("res/github_response/pulls_list_commits_on_a_pull_request.json")

        // when:
        def commits = dataReader.pullCommits(data)

        // then:
        assertThat(commits, Matchers.contains(
            new Commit(null, "Fix all the bugs", "Monalisa Octocat", null, "octocat")
        ))
    }

    @Test
    void "tags"() {
        // given:
        def data = loadJsonFixture("res/github_response/tags_list_tags.json")

        // when:
        def releases = dataReader.tags(data)

        // then:
        assertThat(releases, Matchers.contains(
            new Release("v0.1", "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc")
        ))
    }

    private Object loadJsonFixture(String resourcePath) {
        def inputStream = this.class.classLoader.getResourceAsStream(resourcePath)
        return new JsonSlurper().parse(inputStream)
    }

}