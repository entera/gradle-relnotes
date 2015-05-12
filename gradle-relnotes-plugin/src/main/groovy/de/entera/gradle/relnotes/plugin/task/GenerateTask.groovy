package de.entera.gradle.relnotes.plugin.task

import de.entera.gradle.relnotes.adapter.github.DataProvider
import de.entera.gradle.relnotes.adapter.github.DataProvider.ReleaseNotesConfig
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class GenerateTask extends ConventionTask {

    //---------------------------------------------------------------------------------------------
    // FIELDS.
    //---------------------------------------------------------------------------------------------

    String authToken

    String repository

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    @TaskAction
    void generate() {
        def releaseNotesConfig = new ReleaseNotesConfig(
            authToken: this.authToken,
            repository: this.repository,
        )
        DataProvider.printReleaseNotes(releaseNotesConfig)
    }

}
