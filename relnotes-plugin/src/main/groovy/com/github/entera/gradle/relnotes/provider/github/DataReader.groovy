package com.github.entera.gradle.relnotes.provider.github

import com.github.entera.gradle.relnotes.notes.Commit
import com.github.entera.gradle.relnotes.notes.PullRequest
import com.github.entera.gradle.relnotes.notes.Release

import static java.time.ZonedDateTime.parse

class DataReader {

    Commit commit(Object data) {
        data.with {
            def commit = new Commit(
                authorLogin: it.author.login, // sometimes undefined
                authorName: it.commit.author.name,
                message: it.commit.message,
            )
            return commit
        }
    }

    List<Commit> commits(Object data) {
        def commits = []
        data.each {
            def commit = new Commit(
                authorLogin: it.author.login, // sometimes undefined
                authorName: it.commit.author.name,
                message: it.commit.message,
            )
            commits << commit
        }
        return commits
    }

    List<PullRequest> pulls(Object data) {
        def pulls = []
        data.each {
            if (it.merged_at) {
                def pull = new PullRequest(
                    //it.id
                    number: it.number,
                    title: it.title,
                    mergedAt: parse(it.merged_at as String)
                )
                pulls << pull
            }
        }
        return pulls
    }

    List<Commit> pullCommits(Object data) {
        def pullCommits = []
        data.each {
            if (it.parents.size() <= 1) {
                def commit = new Commit(
                    authorLogin: it.author.login, // sometimes undefined
                    authorName: it.commit.author.name,
                    message: it.commit.message,
                    pullNumber: it.number ? it.number.toString() : null
                )
                pullCommits << commit
            }
        }
        return pullCommits
    }


    List<Release> tags(Object data) {
        def tags = []
        data.each {
            def release = new Release(
                tagName: it.name,
                commitSha: it.commit.sha,
            )
            tags << release
        }
        return tags
    }

}
