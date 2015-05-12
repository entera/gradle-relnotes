package de.entera.gradle.relnotes.plugin.task

import de.entera.gradle.relnotes.adapter.github.DataProvider
import de.entera.gradle.relnotes.adapter.github.DataProvider.ReleaseNotesConfig
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class GenerateTask extends ConventionTask {

    //---------------------------------------------------------------------------------------------
    // FIELDS.
    //---------------------------------------------------------------------------------------------

    String githubKey

    String githubRepo

    @OutputFile
    File targetFile

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    @TaskAction
    void generate() {
        def releaseNotesConfig = new ReleaseNotesConfig(
            githubKey: this.githubKey,
            githubRepo: this.githubRepo,
            targetFile: this.targetFile
        )
        DataProvider.printReleaseNotes(releaseNotesConfig)
    }

}
