package com.github.entera.gradle.relnotes

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer

class RelnotesPlugin implements Plugin<Project> {

    //---------------------------------------------------------------------------------------------
    // CONSTANTS.
    //---------------------------------------------------------------------------------------------

    static final String RELNOTES_CONFIG = "relnotes"

    //---------------------------------------------------------------------------------------------
    // PRIVATE FIELDS.
    //---------------------------------------------------------------------------------------------

    private Project project
    private SourceSetContainer sourceSets

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    void apply(Project project) {
        this.project = project

        // Apply JavaPlugin to ensure that the source sets container and jar task are available.
        this.project.plugins.apply(JavaPlugin)
        this.sourceSets = this.project.sourceSets

    }

}
