package com.github.entera.gradle.relnotes.provider.github

import groovy.json.JsonSlurper

import com.github.entera.gradle.relnotes.notes.model.Author
import com.github.entera.gradle.relnotes.notes.model.Commit
import com.github.entera.gradle.relnotes.notes.model.PullRequest
import com.github.entera.gradle.relnotes.notes.model.Release
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

    @Test
    void "commit"() {
        // given:
        def data = loadJsonFixture("res/github_response/commit_get_a_single_commit.json")

        // when:
        def commit = dataReader.commit(data)

        // then:
        assertThat(commit, Matchers.is(
            new Commit("6dcb09b5b57875f334f61aebed695e2e4193db5e", "Fix all the bugs",
                "Monalisa Octocat", "support@github.com", "octocat", parse("2011-04-14T16:00:49Z"))
        ))
    }

    @Test
    void "commits"() {
        // given:
        def data = loadJsonFixture("res/github_response/commit_list_commits_on_a_repository.json")

        // when:
        def commits = dataReader.commits(data)

        // then:
        assertThat(commits, Matchers.contains(
            new Commit("6dcb09b5b57875f334f61aebed695e2e4193db5e", "Fix all the bugs",
                "Monalisa Octocat", "support@github.com", "octocat", parse("2011-04-14T16:00:49Z"))
        ))
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
            new Commit("6dcb09b5b57875f334f61aebed695e2e4193db5e", "Fix all the bugs",
                "Monalisa Octocat", "support@github.com", "octocat", parse("2011-04-14T16:00:49Z"))
        ))
    }

    @Test
    void "user()"() {
        // given:
        def data = loadJsonFixture("res/github_response/user_get_a_single_user.json")

        // when:
        def user = dataReader.user(data)

        // then:
        assertThat(user, Matchers.is(
            new Author("monalisa octocat", "octocat@github.com", "octocat")
        ))
    }

    @Test
    void "linkRels"() {
        // given:
        def data = loadTextFixture("res/github_response/response_header.txt")

        // when:
        def linkRels = dataReader.linkRels(data)

        // then:
        assertThat(linkRels, Matchers.hasEntry("next", "https://api.github.com/resource?page=2"))
        assertThat(linkRels, Matchers.hasEntry("last", "https://api.github.com/resource?page=5"))
    }

    private String loadTextFixture(String resourcePath) {
        def inputStream = this.class.classLoader.getResourceAsStream(resourcePath)
        return inputStream.getText()
    }

    private Object loadJsonFixture(String resourcePath) {
        def inputStream = this.class.classLoader.getResourceAsStream(resourcePath)
        return new JsonSlurper().parse(inputStream)
    }
}
