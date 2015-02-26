package com.github.entera.gradle.relnotes.adapter.git

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.github.entera.gradle.relnotes.model.Commit
import com.github.entera.gradle.relnotes.model.Release

class GitDataProvider {
    static final List<String> fieldKeys = ["%H", "%an", "%ae", "%ci", "%s"]
    static final String fieldSeparator = "@@field@@"
    static final String commitSeparator = "@@commit@@"

    GitCommands gitCommands

    List<Release> releases() {
        def referencesRegexp = /(?m)^(?<hash>\w+) (?<ref>\S+)$/
        def referencesOutput = gitCommands.showReferences()
        List<Release> releases = []
        referencesOutput.eachMatch(referencesRegexp) { text, commitSha, tagRef ->
            releases << new Release(commitSha: commitSha, tagName: tagRef)
        }

        for (release in releases) {
            def commitReference = release.commitSha
            def commitFormat = buildCommitFormat(fieldKeys, fieldSeparator)
            def commitOptions = "--pretty=format:${commitFormat}"
            def commitOutput = gitCommands.logCommit(commitReference, commitOptions)
            def commitFields2 = parseCommitString(commitOutput, fieldKeys, fieldSeparator)

            def commit = new Commit(
                sha: commitFields2["%H"].trim(),
                authorName: commitFields2["%an"],
                authorLogin: commitFields2["%ae"],
                committedAt: toZuluTime(parseCommitDateTime(commitFields2["%ci"])),
                message: commitFields2["%s"],
            )
            release.releasedAt = commit.committedAt
        }

        return releases
    }

    void commits(String headCommit,
                 String baseCommit) {
        //--no-merges
        def logFormat = buildCommitFormat(fieldKeys, fieldSeparator) + commitSeparator
        def logOptions = "--pretty=\"format:${logFormat}\""
        //def logOutput = gitCommands.logCommits("HEAD~2", "HEAD", logOptions)
        def logOutput = gitCommands.logCommits(baseCommit, headCommit, logOptions)

        List<Commit> commits = []
        def commitStrings = logOutput.split(commitSeparator)
        println commitStrings.size()

        for (String commitString : commitStrings) {
            def commitFields = parseCommitString(commitString, fieldKeys, fieldSeparator)
            commits << new Commit(
                sha: commitFields["%H"],
                authorName: commitFields["%an"],
                authorEmail: commitFields["%ae"],
                committedAt: parseCommitDateTime(commitFields["%ci"]),
                message: commitFields["%s"],
            )
        }

        commits.each { println it }

        def commitsByAuthor = commits
            .groupBy { it.authorEmail }
            .collectEntries { key, value -> [key, value.size()] }
            .sort { entry0, entry1 -> entry1.value <=> entry0.value }
        commitsByAuthor.each { key, value -> println "$key: $value commits" }

        // author name -> author email -> author login
        // author name (author login)
    }

    private ZonedDateTime parseCommitDateTime(String logDateTime) {
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
        return ZonedDateTime.parse(logDateTime, formatter)
    }

    private ZonedDateTime toZuluTime(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneId.of("Z"))
    }

    private String buildCommitFormat(List<String> fieldKeys,
                                     String fieldSeparator) {
        return fieldKeys.join(fieldSeparator)
    }

    private Map<String, String> parseCommitString(String commitString,
                                                  List<String> fieldKeys,
                                                  String fieldSeparator) {
        def fieldValues = commitString.split(fieldSeparator)
        return (0..<fieldKeys.size()).collectEntries { int index ->
            [fieldKeys[index], fieldValues[index]]
        }
    }
}
