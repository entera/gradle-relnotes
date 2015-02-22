package com.github.entera.gradle.relnotes

import com.github.entera.gradle.relnotes.task.ReleaseNotesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer

class ReleaseNotesPlugin implements Plugin<Project> {

    //---------------------------------------------------------------------------------------------
    // CONSTANTS.
    //---------------------------------------------------------------------------------------------

    static final String RELNOTES_CONFIG = "releaseNotes"

    static final String RELNOTES_TASK = "releaseNotes"

    //---------------------------------------------------------------------------------------------
    // PRIVATE FIELDS.
    //---------------------------------------------------------------------------------------------

    private Project project
    private SourceSetContainer sourceSets

    private ReleaseNotesTask releaseNotesTask

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    void apply(Project project) {
        this.project = project

        // Apply JavaPlugin to ensure that the source sets container and jar task are available.
        this.project.plugins.apply(JavaPlugin)
        this.sourceSets = this.project.sourceSets

        this.registerExtensions()
        this.registerTasks()

        this.project.afterEvaluate {
            this.configureTasks()
        }
    }

    //---------------------------------------------------------------------------------------------
    // PRIVATE METHODS.
    //---------------------------------------------------------------------------------------------

    private void registerExtensions() {
        this.project.extensions.create(RELNOTES_CONFIG, ReleaseNotesConfig)
    }

    private void registerTasks() {
        this.releaseNotesTask = this.project.task(
            type: ReleaseNotesTask, RELNOTES_TASK
        ) as ReleaseNotesTask
    }

    private void configureTasks() {
        def relnotesConfig = this.project.extensions.getByName(RELNOTES_CONFIG) as ReleaseNotesConfig

        this.project.configure(this.releaseNotesTask) {
            println relnotesConfig
        }
    }

}
