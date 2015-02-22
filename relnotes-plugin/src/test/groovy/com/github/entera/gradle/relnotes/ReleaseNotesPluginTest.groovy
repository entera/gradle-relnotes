package com.github.entera.gradle.relnotes
import com.github.entera.gradle.relnotes.task.ReleaseNotesTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.instanceOf
import static org.junit.Assert.assertThat

class ReleaseNotesPluginTest {

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
        assertThat(project.tasks.releaseNotes, instanceOf(ReleaseNotesTask))
    }

    @Test
    public void should_register_extensions() {
        // expect:
        assertThat(project.extensions.releaseNotes, instanceOf(ReleaseNotesConfig))
    }

}