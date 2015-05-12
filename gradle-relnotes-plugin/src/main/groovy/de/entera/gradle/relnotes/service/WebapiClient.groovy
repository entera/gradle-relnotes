package de.entera.gradle.relnotes.service

import groovy.json.JsonSlurper

class WebapiClient {
    WebapiResponse json(WebapiRequest request) {
        def connection = openConnection(request)
        assignRequestParams(request, connection)
        def data = retrieveResponseData(connection)
        def headers = collectResponseHeaders(connection)
        def response = new WebapiResponse(data: data, headers: headers)
        return response
    }

    private URLConnection openConnection(WebapiRequest request) {
        return request.url.openConnection()
    }

    private assignRequestParams(WebapiRequest request,
                                URLConnection connection) {
        request.params.each { key, value ->
            connection.setRequestProperty(key, value)
        }
    }

    private Object retrieveResponseData(URLConnection connection) {
        def slurper = new JsonSlurper()
        return slurper.parse(connection.inputStream)
    }

    private Map<String, String> collectResponseHeaders(URLConnection connection) {
        return connection.headerFields.keySet().collectEntries { String key ->
            [key, connection.getHeaderField(key)]
        }
    }
}
