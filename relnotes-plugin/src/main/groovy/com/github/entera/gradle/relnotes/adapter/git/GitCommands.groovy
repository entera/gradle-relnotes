package com.github.entera.gradle.relnotes.adapter.git

import com.github.entera.gradle.relnotes.service.CommandRunner

class GitCommands {
    //static Logger logger = LogManager.getLogger(this.class)

    CommandRunner cmdRunner

    //current commit hash: "git rev-list --max-count=1 HEAD" || "git rev-parse --verify HEAD"
    //first commit hash: "git rev-list --max-parents=0 HEAD"

    String showReferences() {
        def process = cmdRunner.run("git", "show-ref", "--tags", "--dereference")
        def output = process.inputStream.text
        process.waitFor()
        return output
    }

    String fetchReferences(String repository,
                           String revision0,
                           String revision1) {
        def process = cmdRunner.run("git", "fetch", repository,
            "+refs/tags/${revision0}:refs/tags/${revision1}")
        def output = process.inputStream.text
        process.waitFor()
        return output
    }

    String logCommit(String reference,
                     String options) {
        def process = cmdRunner.run("git", "log", "-1", options, reference)
        def output = process.inputStream.text
        process.waitFor()
        return output
    }

    String logCommits(String revision0,
                      String revision1,
                      String options) {
        def process = cmdRunner.run("git", "--no-pager", "log", options,
            "${revision0}..${revision1}")
        def output = process.inputStream.text
        process.waitFor()
        return output
    }
}
