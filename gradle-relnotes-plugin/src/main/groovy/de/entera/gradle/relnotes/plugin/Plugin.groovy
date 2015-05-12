package de.entera.gradle.relnotes.plugin

import de.entera.gradle.relnotes.plugin.task.GenerateTask
import org.gradle.api.Project

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

    private GenerateTask generateTask

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    void apply(Project project) {
        this.project = project

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
            this.generateTask.githubKey = config.githubKey
            this.generateTask.githubRepo = config.githubRepo
            this.generateTask.targetFile = config.targetFile
        }
    }

}
