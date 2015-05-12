package de.entera.gradle.relnotes.plugin

import de.entera.gradle.relnotes.plugin.task.GenerateTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer

class Plugin implements org.gradle.api.Plugin<Project> {

    //---------------------------------------------------------------------------------------------
    // CONSTANTS.
    //---------------------------------------------------------------------------------------------

    static final String CONFIG_NAME = "releaseNotes"

    static final String GENERATE_TASK = "generateReleaseNotes"

    //---------------------------------------------------------------------------------------------
    // PRIVATE FIELDS.
    //---------------------------------------------------------------------------------------------

    private Project project
    private SourceSetContainer sourceSets

    private GenerateTask generateTask

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
        this.project.extensions.create(CONFIG_NAME, Config)
    }

    private void registerTasks() {
        this.generateTask = this.project.task(
            type: GenerateTask, GENERATE_TASK,
            group: "publishing"
        ) as GenerateTask
    }

    private void configureTasks() {
        def config = this.project.extensions.getByName(CONFIG_NAME) as Config

        this.project.configure(this.generateTask) {
            this.generateTask.authToken = config.authToken
            this.generateTask.repository = config.repository
        }
    }

}
