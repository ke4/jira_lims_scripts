package uk.ac.sanger.scgcf.jira.lims.utils

import groovy.util.logging.Slf4j
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
@Slf4j(value = "LOG")
class RestService {

    HTTPBuilder httpBuilder

    public RestService(String requestBaseURL) {
        LOG.debug("Connecting to: $requestBaseURL service")
        this.httpBuilder = new HTTPBuilder(requestBaseURL)
    }

    public Map request(Method method, Map<?, ?> requestHeaders, ContentType contentType, String servicePath, def requestBody) {
        LOG.debug("Sending a ${method.name()} request to $servicePath resource")
        httpBuilder.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }
        httpBuilder.handler.failure = httpBuilder.handler.success

        def map = new HashMap<String, Object>()
        try{
            map = httpBuilder.request(method, contentType) { req ->
                uri.path = servicePath
                headers = requestHeaders
                body = requestBody
            }
        } catch(ClientProtocolException | IOException e) {
            throw e
        }

        map
    }
}
