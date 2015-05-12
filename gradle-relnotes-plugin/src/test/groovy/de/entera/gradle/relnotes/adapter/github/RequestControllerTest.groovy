package de.entera.gradle.relnotes.adapter.github

import java.time.ZonedDateTime

import org.hamcrest.Matchers
import org.junit.Test

import static org.junit.Assert.assertThat

class RequestControllerTest {
    static class RequestContext {
        String commitSha
        Integer pageIndex = 1
        ZonedDateTime dateTime

        Integer numOfEntries = 0
        Integer numOfRequests = 0
    }

    @Test
    void "commitSha"() {
        def controller = new RequestController<RequestContext>(new RequestContext())
        controller.contextPredicate = { ctx ->
            ctx.commitSha != "xxxxxxx" && ctx.pageIndex <= 10
        }

        controller.updateContext { ctx -> ctx.commitSha = "abcd123" }
        assertThat(controller.checkPredicate(), Matchers.is(true))

        controller.updateContext { ctx -> ctx.commitSha = "xxxxxxx" }
        assertThat(controller.checkPredicate(), Matchers.is(false))
        assertThat(controller.context.commitSha, Matchers.is("xxxxxxx"))
    }

    @Test
    void "pageIndex"() {
        def controller = new RequestController<RequestContext>(new RequestContext())
        controller.contextPredicate = { ctx ->
            ctx.commitSha != "xxxxxxx" && ctx.pageIndex <= 10
        }

        while (controller.checkPredicate()) {
            assertThat(controller.checkPredicate(), Matchers.is(true))
            controller.updateContext { ctx -> ctx.pageIndex += 1 }
        }
        assertThat(controller.checkPredicate(), Matchers.is(false))
        assertThat(controller.context.pageIndex, Matchers.is(11))
    }
}
