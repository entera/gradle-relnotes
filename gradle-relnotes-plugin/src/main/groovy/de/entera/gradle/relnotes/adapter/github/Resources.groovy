package de.entera.gradle.relnotes.adapter.github

import java.time.ZonedDateTime

class Resources {
    static UrlBuilder tags(String repos) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("tags")
            .params("per_page=100")
    }

    static UrlBuilder commits(String repos) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("commits")
            .params("per_page=100&sha=master")
    }

    static UrlBuilder commitsSince(String repos,
                                   ZonedDateTime since,
                                   ZonedDateTime until) {
        // TODO: stop pagination at "since"
        return UrlBuilder.build()
            .repo(repos)
            .resource("commits")
            .params("per_page=100&sha=master")
    }

    static UrlBuilder commit(String repos,
                             String sha) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("commits/${sha}")
    }

    static UrlBuilder pulls(String repos) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("pulls")
            .params("per_page=100&state=all&base=master&sort=created&direction=desc")
    }

    static UrlBuilder pullsSince(String repos,
                                 ZonedDateTime since) {
        // TODO: stop pagination at "since"
        return UrlBuilder.build()
            .repo(repos)
            .resource("pulls")
            .params("per_page=100&state=all&base=master&sort=updated&direction=desc")
    }

    static UrlBuilder pullCommits(String repos,
                                  String pullNumber) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("pulls/${pullNumber}/commits")
            .params("per_page=100")
    }

    static UrlBuilder issues(String repos) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("issues")
            .params("per_page=100&filter=all&state=all&sort=created&direction=desc")
    }

    static UrlBuilder issuesSince(String repos,
                                  ZonedDateTime since) {
        // TODO: stop pagination at "since"
        return UrlBuilder.build()
            .repo(repos)
            .resource("issues")
            .params("per_page=100&filter=all&state=all&sort=updated&direction=desc&since=date")
    }

    static UrlBuilder user(String username) {
        return UrlBuilder.build()
            .resource("users/${username}")
    }

    static UrlBuilder compare(String repos,
                              String base,
                              String head) {
        return UrlBuilder.build()
            .repo(repos)
            .resource("compare/${base}...${head}")
    }
}
