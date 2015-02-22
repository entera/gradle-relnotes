package com.github.entera.gradle.relnotes.service

import org.hamcrest.Matchers
import org.junit.Test

import static org.junit.Assert.assertThat
import static CommandRunner.hiddenArg

class CommandRunnerTest {

    @Test
    void "run()"() {
        // given:
        def runner = new CommandRunner()
        def process = runner.run "git", hiddenArg("log"), "--oneline"

        // expect:
        assertThat(process.waitFor(), Matchers.is(0))
        assertThat(process.alive, Matchers.is(false))
    }

}
