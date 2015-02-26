package com.github.entera.gradle.relnotes.adapter.git

import com.github.entera.gradle.relnotes.service.CommandRunner
import org.junit.Before
import org.junit.Test

class GitDataProviderTest {
    CommandRunner cmdRunner
    GitCommands gitCommands
    GitDataProvider dataProvider

    @Before
    void setup() {
        cmdRunner = new CommandRunner()
        cmdRunner.directory = new File(".")
        println cmdRunner.directory.absolutePath
        cmdRunner.logging = false

        gitCommands = new GitCommands(cmdRunner: cmdRunner)
        dataProvider = new GitDataProvider(gitCommands: gitCommands)
    }

    @Test
    void "releases()"() {
        println "=" * 40
        def releases = dataProvider.releases()
        releases.each { println it }
    }

    @Test
    void "commits()"() {
        println "=" * 40
        def releases = dataProvider.releases()
        dataProvider.commits("master", releases[1].commitSha)

        println "=" * 40
        dataProvider.commits("HEAD", "HEAD~2")
    }
}
