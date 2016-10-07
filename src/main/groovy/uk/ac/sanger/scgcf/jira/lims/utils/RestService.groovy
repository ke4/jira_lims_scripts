package uk.ac.sanger.scgcf.jira.lims.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.client.ClientProtocolException
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader

/**
 * The {@code RestService} class represents a utility class that can communicate
 * with REST services with HTTP methods.
 *
 * Created by ke4 on 05/10/2016.
 */
class RestService {

    public Map request(Method method, ContentType contentType, String requestPath, def requestBody) {
        HTTPBuilder http = new HTTPBuilder(ConfigReader.getSequencescapeDetails()['baseUrl'])
        http.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }
        http.handler.failure = http.handler.success

        def map = new HashMap<String, Object>()
        try{
            map = http.request(method, contentType) { req ->
                uri.path = "${ConfigReader.getSequencescapeDetails()['apiVersion']}/$requestPath"
                headers.Cookie = "api_key=${ConfigReader.getSequencescapeDetails()['apiKey']}"
                body = requestBody
            }
        } catch(ClientProtocolException | IOException e) {
            throw e
        }

        map
    }
}
