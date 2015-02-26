package com.github.entera.gradle.relnotes.provider.github

import com.github.entera.gradle.relnotes.notes.model.Author
import com.github.entera.gradle.relnotes.notes.model.Commit
import com.github.entera.gradle.relnotes.notes.model.PullRequest
import com.github.entera.gradle.relnotes.notes.model.Release

import static java.time.ZonedDateTime.parse

class DataReader {
    Release tag(Object data) {
        data.with {
            def release = new Release(
                tagName: it.name,
                commitSha: it.commit.sha,
            )
            return release
        }
    }

    List<Release> tags(Object data) {
        def tags = []
        data.each {
            def release = tag(it)
            tags << release
        }
        return tags
    }

    Commit commit(Object data) {
        data.with {
            def commit = new Commit(
                sha: it.sha,
                authorLogin: it.author?.login, // sometimes undefined
                authorName: it.commit.author.name,
                authorEmail: it.commit.author.email,
                message: it.commit.message,
                committedAt: parse(it.commit.committer.date as String),
                numOfParents: it.parents.size()
            )
            return commit
        }
    }

    List<Commit> commits(Object data) {
        def commits = []
        data.each {
            def commit = commit(it)
            commits << commit
        }
        return commits
    }

    PullRequest pull(Object data) {
        data.with {
            def pull = new PullRequest(
                number: it.number,
                title: it.title.trim(),
                mergedAt: parse(it.merged_at as String)
            )
            return pull
        }
    }

    List<PullRequest> pulls(Object data) {
        def pulls = []
        data.each {
            if (it.merged_at) {
                def pull = pull(it)
                pulls << pull
            }
        }
        return pulls
    }

    List<Commit> pullCommits(Object data) {
        def pullCommits = []
        data.each {
            if (it.parents.size() <= 1) {
                def commit = commit(it)
                pullCommits << commit
            }
        }
        return pullCommits
    }

    Author user(Object data) {
        data.with {
            def author = new Author(
                login: it.login,
                name: it.name,
                email: it.email
            )
            return author
        }
    }

    Map<String, String> linkRels(String header) {
        def matcher = header =~ /(?m)<(?<url>\S+)>;\s*?rel="(?<rel>\S+)"/
        return matcher.collect { [it[2], it[1]] }.collectEntries()
    }
}
