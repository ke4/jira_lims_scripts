package uk.ac.sanger.scgcf.jira.lims.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.client.ClientProtocolException

/**
 * The {@code RestService} class represents a utility class that can communicate
 * with REST services with HTTP methods.
 *
 * Created by ke4 on 05/10/2016.
 */
class RestService {

    public Map request(Method method, ContentType contentType, String requestPath, Map<String, String> params) {
        HTTPBuilder http = new HTTPBuilder(SequencescapeConstants.SS_BASE_URL)
        http.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }
        http.handler.failure = http.handler.success

        def map = new HashMap<String, Object>()
        try{
            map = http.request(method, contentType) { req ->
                uri.path = "${SequencescapeConstants.API_VERSION}$requestPath"
                headers.Cookie = "api_key=${SequencescapeConstants.API_KEY}"
                body = [
                        "search": [
                                "name": params['projectname']
                        ]
                ]
            }
        } catch(ClientProtocolException | IOException e) {
            throw e
        }

        map
    }
}
