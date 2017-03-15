package uk.ac.sanger.scgcf.jira.lims.services

import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.configurations.JiraLimsServices
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.RestService
import uk.ac.sanger.scgcf.jira.lims.utils.ValidatorExceptionHandler

import static groovyx.net.http.ContentType.JSON

/**
 * The <@code LabelPrinter> class communicating with the Print My Barcode micro service
 * to print given label(s).
 * If the service is down it will response to a user with a friendly error message.
 *
 * Created by ke4 on 14/03/2017.
 */
@Slf4j(value = "LOG")
class LabelPrinter {

    def static printMyBarcodeDetails = ConfigReader.getServiceDetails(JiraLimsServices.PRINT_MY_BARCODE)
    RestService restService = new RestService(getPrintMyBarcodeServicePath())
    def responseCode

    /**
     * Print a given label calling the Print My Barcode service.
     *
     * @param requestBody contains the body of the request,
     * which consists of a header, footer and the labels sections.
     *
     * @return the response body of the REST call
     */
    public def printLabel(def requestBody) {
        callPrintMyBarcode(Method.POST, requestBody, printLabelPath())
    }

    private def callPrintMyBarcode(Method method, def requestBody, String servicePath) {
        LOG.debug("request body: ${requestBody.toString()}")
        def responseMap = restService.request(method, [:], JSON, servicePath, requestBody)
        def response = responseMap['response']
        def reader = responseMap['reader']
        LOG.debug(responseMap.toString())
        responseCode = response.status

        if (!(responseCode in 200..<300)) {
            def errorMessage
            def additionalMessage
            if (responseCode == 503) {
                errorMessage = "Communication with Print-My_Barcode app has failed (HTTP status code: $responseCode)."
                additionalMessage = "The error message is: $reader. URL: ${restService.httpBuilder.uri}/${servicePath}, Request: $requestBody".toString()
            } else {
                errorMessage = "Print-My-Barcode app responded with a failure (HTTP status code: $responseCode)."
                additionalMessage = "The error message is: $reader. URL: ${restService.httpBuilder.uri}/${servicePath}, Request: $requestBody".toString()
            }

            def printMyBarcodeError = new RestServiceException(errorMessage)
            ValidatorExceptionHandler.throwAndLog(printMyBarcodeError, errorMessage, additionalMessage)
        }

        reader
    }

    public static String printLabelPath() {
        "/${printMyBarcodeDetails['printLabelPath']}".toString()
    }

    private static String getPrintMyBarcodeServicePath() {
        String.format("%s:%s/%s%s",
                printMyBarcodeDetails['baseUrl'],
                printMyBarcodeDetails['port'],
                printMyBarcodeDetails['contextPath'],
                printMyBarcodeDetails['apiVersion'],
        )
    }
}
