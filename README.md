# gradle-relnotes

Gradle plugins for release notes. &mdash; https://github.com/entera/gradle-relnotes


## Status

This project was started on **December 10, 2014** and is **not ready for production**, yet. :smiley_cat:

[![](https://img.shields.io/travis/entera/gradle-relnotes/master.svg?label=travis)][Travis CI]
[![](https://img.shields.io/maven-central/v/de.entera/gradle-relnotes-plugin.svg?label=bintray)][Bintray JCenter]
[![](https://img.shields.io/maven-central/v/de.entera/gradle-relnotes-plugin.svg?label=maven)][Maven Central]

[Travis CI]: https://travis-ci.org/entera/gradle-relnotes "Travis CI"
[Bintray JCenter]: https://bintray.com/entera/gradle-relnotes "Bintray JCenter"
[Maven Central]: https://search.maven.org/#search|ga|1|de.entera+gradle-relnotes-plugin "Maven Central"

Gradle plugin page: https://plugins.gradle.org/plugin/de.entera.relnotes


## Features

_TBD._


## Usage

**Example 1: `build.gradle`**

~~~groovy

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // use the gradle relnotes plugin to generate release notes.
        classpath "de.entera:gradle-relnotes-plugin:0.1.0"
    }
}

// provide generateReleaseNotes task.
apply plugin: "de.entera.relnotes"

// configure generateReleaseNotes task.
releaseNotes {
    targetFile rootProject.file("CHANGES.md")
    githubRepo "entera/gradle-relnotes"
    githubKey "GITHUB_KEY"
}
~~~


## Motivation

_TBD._


## Contribute

_TBD._


## License

~~~
Copyright 2014-2015 Benjamin Gudehus <hastebrot@gmail.com>

Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
European Commission - subsequent versions of the EUPL (the "Licence"); You may
not use this work except in compliance with the Licence.

You may obtain a copy of the Licence at:
http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the Licence for the
specific language governing permissions and limitations under the Licence.
~~~
