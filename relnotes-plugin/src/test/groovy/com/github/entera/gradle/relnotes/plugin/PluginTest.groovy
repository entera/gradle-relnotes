package com.github.entera.gradle.relnotes.plugin

import com.github.entera.gradle.relnotes.plugin.task.GenerateTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.instanceOf
import static org.junit.Assert.assertThat

class PluginTest {

    //---------------------------------------------------------------------------------------------
    // FIELDS.
    //---------------------------------------------------------------------------------------------

    Project project

    //---------------------------------------------------------------------------------------------
    // FIXTURE METHODS.
    //---------------------------------------------------------------------------------------------

    @Before
    void setup() {
        def builder = ProjectBuilder.builder()
        //builder.withProjectDir()
        project = builder.build()
        project.apply(plugin: "relnotes-plugin")
    }

    //---------------------------------------------------------------------------------------------
    // FEATURE METHODS.
    //---------------------------------------------------------------------------------------------

    @Test
    public void should_register_tasks() {
        // expect:
        assertThat(project.tasks.generateReleaseNotes, instanceOf(GenerateTask))
    }

    @Test
    public void should_register_extensions() {
        // expect:
        assertThat(project.extensions.releaseNotes, instanceOf(Config))
    }

}
