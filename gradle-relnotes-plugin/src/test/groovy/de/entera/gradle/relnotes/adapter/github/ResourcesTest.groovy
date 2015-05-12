package de.entera.gradle.relnotes.adapter.github

import org.hamcrest.Matchers
import org.junit.Test

import static org.junit.Assert.assertThat

class ResourcesTest {
    @Test
    void "should build url for tags"() {
        // expect:
        def resource = Resources.tags("testfx/testfx")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/tags?per_page=100"
        )
    }

    @Test
    void "should build url for commits"() {
        // expect:
        def resource = Resources.commits("testfx/testfx")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/commits?per_page=100&sha=master"
        )
    }

    @Test
    void "should build url for commit"() {
        // expect:
        def resource = Resources.commit("testfx/testfx", "1234abc")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/commits/1234abc"
        )
    }

    @Test
    void "should build url for pulls"() {
        // expect:
        def resource = Resources.pulls("testfx/testfx")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/pulls?per_page=100&state=all&" +
                "base=master&sort=created&direction=desc"
        )
    }

    @Test
    void "should build url for pullCommits"() {
        // expect:
        def resource = Resources.pullCommits("testfx/testfx", "123")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/pulls/123/commits?per_page=100"
        )
    }

    @Test
    void "should build url for issues"() {
        // expect:
        def resource = Resources.issues("testfx/testfx")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/issues?per_page=100&filter=all&" +
                "state=all&sort=created&direction=desc"
        )
    }

    @Test
    void "should build url for user"() {
        // expect:
        def resource = Resources.user("octocat")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/users/octocat"
        )
    }

    @Test
    void "should build url for compare"() {
        // expect:
        def resource = Resources.compare("testfx/testfx", "master~5", "master")
        assertThat resource.toUrl().toString(), Matchers.is(
            "https://api.github.com/repos/testfx/testfx/compare/master~5...master"
        )
    }
}
