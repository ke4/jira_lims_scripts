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
    boolean isLabelPrintingOn = ConfigReader.isLabelPrintingOn()
    RestService restService = new RestService(getPrintMyBarcodeServicePath())
    def responseCode

    /**
     * Print a given label calling the Print My Barcode service.
     *
     * @note Currently the Print My Barcode application does not support offline printing mode.
     * It always sends the request to printer. Printing a label is expensive, so we added a setting
     * to our config file to be able to turn off label printing. If the printing is turned off this service
     * won't call the Print My Barcode application for the above reason, it will just respond with {code true} value.
     * We have requested a feature change from the Print My Barcode team for the above purpose.
     *
     * @param requestBody contains the body of the request,
     * which consists of a header, footer and the labels sections.
     *
     * @return the response body of the REST call
     */
    public def printLabel(def requestBody) {
        if (isLabelPrintingOn) {
            callPrintMyBarcode(Method.POST, requestBody, printLabelPath())
        } else {
            // @see documentation
            true
        }
    }

    /**
     * Calling the Print My Barcode service with the given Method (POST or GET request),
     * request body (according to the method) and URI to the end point.
     *
     * @param method of the request (POST or GET)
     * @param requestBody to send to the resource's end point
     * @param servicePath the end point of the resource
     * @return true if the response was OK, otherwise throws an InvalidInputException to catch by Jira.
     * Jira will pop up a modal dialog on the UI with an error message coming from the caught exception.
     */
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

        true
    }

    /**
     * Return the print label service URI.
     * @return the print label service URI.
     */
    public static String printLabelPath() {
        "${getContextPath()}/${printMyBarcodeDetails['printLabelPath']}".toString()
    }

    /**
     * Returns the context path.
     *
     * @return the context path.
     */
    public static String getContextPath() {
        String.format("%s%s",
            printMyBarcodeDetails['contextPath'],
            printMyBarcodeDetails['apiVersion']
        )
    }

    /**
     * Returns the URI for Print My Barcode service.
     * @return the URI for Print My Barcode service.
     */
    public static String getPrintMyBarcodeServicePath() {
        String.format("%s:%s",
            printMyBarcodeDetails['baseUrl'],
            printMyBarcodeDetails['port']
        )
    }
}
