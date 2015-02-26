package com.github.entera.gradle.relnotes.adapter.github

import java.time.ZoneId
import java.time.ZonedDateTime

import com.github.entera.gradle.relnotes.model.ReleaseNotes
import com.github.entera.gradle.relnotes.model.Author
import com.github.entera.gradle.relnotes.model.Commit
import com.github.entera.gradle.relnotes.model.PullRequest
import com.github.entera.gradle.relnotes.model.Release
import com.github.entera.gradle.relnotes.printer.ReleaseNotesPrinter
import com.github.entera.gradle.relnotes.service.WebapiClient
import com.github.entera.gradle.relnotes.service.WebapiRequest
import com.github.entera.gradle.relnotes.service.WebapiResponse

class DataProvider {
    static void main(String[] args) {
        def authToken = args.size() > 0 ? args[0] : null
        if (authToken == null) {
            def properties = fetchProperties()
            authToken = properties.getProperty("githubAuthToken")
        }

        def baseUrl = "https://github.com"
        def repository = "TestFX/TestFX"
        def repositoryUrl = "https://github.com/TestFX/TestFX"

        def service = new ServiceHandler(
            authToken: authToken
        )
        def provider = new DataProvider(
            service: service, reader: new DataReader(), repository: repository
        )

        def releases = provider.fetchReleases()
        releases = provider.assignReleaseDates(releases)
        releases.each { println it }

        def commits = provider.fetchCommits()
        def authors = provider.extractAuthors(commits)
        authors = provider.assignAuthorLogins(authors)

        def pullRequests = provider.fetchPullRequests()
        def pullCommits = provider.fetchPullCommits(pullRequests)

        def releaseNotes = new ReleaseNotes()
        releaseNotes.releases = releases
        releaseNotes.commits = commits
        releaseNotes.authors = authors
        releaseNotes.pullRequests = pullRequests
        releaseNotes.pullCommits = pullCommits

        def printer = new ReleaseNotesPrinter(baseUrl: baseUrl, repositoryUrl: repositoryUrl)
        printer.releaseNotes = releaseNotes

        printer.assignReleasesToPullRequests()
        printer.assignPullRequestsToPullCommits()
        printer.assignPullRequestMergeToCommits()
        printer.assignReleasesToCommits()
        printer.assignCommitsToAuthors()

        for (release in releases) {
            println printer.generateReleaseNotes(release)
        }

        //def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss'Z'")
        //def dateTime = ZonedDateTime.now(ZoneId.of("Z")).minusDays(30)
        //def since = dateTime.format(formatter)
    }

    static Properties fetchProperties() {
        def propertiesFile = new File("auth.properties")
        def properties = new Properties()
        properties.load(propertiesFile.newReader())
        return properties
    }

    static ZonedDateTime toZuluTime(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneId.of("Z"))
    }

    ServiceHandler service
    DataReader reader
    String repository

    List<Release> fetchReleases() {
        def tagsResource = Resources.tags(this.repository)
        def taggedReleases = this.tags(tagsResource)
        def draftRelease = new Release(
            tagName: "master", releasedAt: toZuluTime(ZonedDateTime.now())
        )
        def releases = []
        releases.add(draftRelease)
        releases.addAll(taggedReleases)
        return releases
    }

    List<Release> assignReleaseDates(List<Release> releases) {
        for (tag in releases) {
            if (tag.commitSha) {
                def commitResource = Resources.commit(this.repository, tag.commitSha)
                def commit = this.tagsCommit(commitResource)
                tag.releasedAt = commit.committedAt
            }
        }
        return releases
    }

    List<Commit> fetchCommits() {
        def commitsResource = Resources.commits(this.repository)
        def commits = this.commits(commitsResource)
        commits = commits.findAll { it.numOfParents == 1 }
        return commits
    }

    List<Author> extractAuthors(List<Commit> commits) {
        def authors = [] as List<Author>
        for (Commit commit in commits) {
            def author = new Author(
                name: commit.authorName, email: commit.authorEmail, login: commit.authorLogin
            )
            authors << author
            commit.refAuthor = author
        }
        authors = authors.unique(false) { it.login ? it.login : it.name }
        return authors
    }

    List<Author> assignAuthorLogins(List<Author> authors) {
        for (Author author in authors) {
            if (author.login) {
                def userResource = Resources.user(author.login)
                def user = this.user(userResource)
                if (user) {
                    author.name = user.name
                    author.email = user.email
                }
            }
        }
        return authors
    }

    List<PullRequest> fetchPullRequests() {
        def pullsResource = Resources.pulls(this.repository)
        def pullRequests = this.pulls(pullsResource)
        return pullRequests
    }

    List<Commit> fetchPullCommits(List<PullRequest> pullRequests) {
        def resultPullCommits = []
        for (pullRequest in pullRequests) {
            def pullCommitsResource = Resources.pullCommits(this.repository, pullRequest.number)
            def pullCommits = this.pullCommits(pullCommitsResource)
            pullCommits.each { it.pullNumber = pullRequest.number as String }
            pullCommits = pullCommits.findAll { it.numOfParents == 1 }
            resultPullCommits.addAll(pullCommits)
        }
        return resultPullCommits
    }

    private Author user(UrlBuilder resource) {
        def request = service.buildRequest(resource.toUrl())
        def response = service.retrieveResponse(request)

        def user = reader.user(response.data)
        return user
    }

    private List<Release> tags(UrlBuilder resource) {
        def request = service.buildRequest(resource.page(1).toUrl())
        def response = service.retrieveResponse(request)

        def tags = reader.tags(response.data)
        return tags
    }

    private Commit tagsCommit(UrlBuilder resource) {
        def request = service.buildRequest(resource.toUrl())
        def response = service.retrieveResponse(request)

        def commit = reader.commit(response.data)
        return commit
    }

    private List<Commit> commits(UrlBuilder resource) {
        def context = new RequestController<RequestContext>(new RequestContext())
        context.contextPredicate = { ctx ->
            ctx.numOfRequests < 50 && !ctx.emptyPage
        }

        def results = []
        while (context.checkPredicate()) {
            def request = service.buildRequest(resource.page(context.context.pageIndex).toUrl())
            def response = service.retrieveResponse(request)

            context.updateContext { ctx -> ctx.numOfRequests += 1 }
            context.updateContext { ctx -> ctx.emptyPage = response.data == [] }
            context.updateContext { ctx -> ctx.pageIndex += 1 }

            def commits = reader.commits(response.data)
            results.addAll(commits)
        }
        return results
    }

    private List<PullRequest> pulls(UrlBuilder resource) {
        def context = new RequestController<RequestContext>(new RequestContext())
        context.contextPredicate = { ctx ->
            ctx.numOfRequests < 50 && !ctx.emptyPage
        }

        def results = []
        while (context.checkPredicate()) {
            def request = service.buildRequest(resource.page(context.context.pageIndex).toUrl())
            def response = service.retrieveResponse(request)

            if (context.context.pageIndex == 1) {
                fetchLastPageIndex(response).ifPresent { int lastPageIndex ->
                    context.contextPredicate = context.contextPredicate.and { ctx ->
                        ctx.pageIndex <= lastPageIndex
                    }
                    return null
                }
            }
            context.updateContext { ctx -> ctx.numOfRequests += 1 }
            context.updateContext { ctx -> ctx.emptyPage = response.data == [] }
            context.updateContext { ctx -> ctx.pageIndex += 1 }

            def pulls = reader.pulls(response.data)
            results.addAll(pulls)
        }
        return results
    }

    private List<Commit> pullCommits(UrlBuilder resource) {
        def request = service.buildRequest(resource.page(1).toUrl())
        def response = service.retrieveResponse(request)

        def pullCommits = reader.pullCommits(response.data)
        return pullCommits
    }

    private Optional<Integer> fetchLastPageIndex(WebapiResponse response) {
        def linkRels = reader.linkRels(response.headers["Link"])

        def pattern = /\bpage=(?<index>\d+)/
        def lastPageIndex = (linkRels["last"] =~ pattern)
        return lastPageIndex.size() > 0 ? Optional.of(lastPageIndex[0][1]) : Optional.empty()
    }

    // https://api.github.com/search/issues?
    //   q=repo:testfx/testfx+is:pr+is:merged+merged:>=2013-01-01+merged:<=2014-01-01

    // - https://developer.github.com/v3/repos/#list-tags
    //   - https://api.github.com/repos/testfx/testfx/tags

    // https://api.github.com/repos/testfx/testfx/commits?
    //   since=YYYY-MM-DDTHH:MM:SSZ&until=YYYY-MM-DDTHH:MM:SSZ&sha=master
    // https://api.github.com/repos/testfx/testfx/pulls?
    //   state=closed&base=master
    // https://api.github.com/repos/testfx/testfx/pulls/:pull_id/commits

    static class RequestContext {
        String commitSha
        Integer pageIndex = 1
        ZonedDateTime dateTime

        boolean emptyPage = false
        Integer numOfEntries = 0
        Integer numOfRequests = 0
    }

    static class ServiceHandler {
        String authToken

        WebapiRequest buildRequest(URL url) {
            def params = [:]
            params["Accept"] = "application/json"
            if (authToken) {
                params["Authorization"] = "token ${authToken}".toString()
            }
            return new WebapiRequest(url, params)
        }

        WebapiResponse retrieveResponse(WebapiRequest request) {
            println "request: ${request.url}"
            return new WebapiClient().json(request)
        }

        void printResponseHeaders(WebapiResponse response) {
            def headers = response.headers
            println "Status: " + headers["Status"]
            println "Link: " + headers["Link"]
            println "X-RateLimit-Limit: " + headers["X-RateLimit-Limit"]
            println "X-RateLimit-Remaining: " + headers["X-RateLimit-Remaining"]
            println "X-RateLimit-Reset: " + headers["X-RateLimit-Reset"]
        }
    }
}
