package uk.ac.sanger.scgcf.jira.lims.services

import groovyx.net.http.Method
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.configurations.JiraLimsServices
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import static groovyx.net.http.ContentType.JSON

/**
 * Created by ke4 on 14/03/2017.
 */
class LabelPrintSpec extends Specification {

    @Shared printMyBarcodeDetails

    def setupSpec() {
        EnvVariableAccess.metaClass.static.getJiraLimsConfigFilePath = { "./src/test/resources/jira_lims_config.json" }
        printMyBarcodeDetails = ConfigReader.getServiceDetails(JiraLimsServices.PRINT_MY_BARCODE)
    }

    def "when Print-My-Barcode service is down we should receive HTTP status 503"() {

        setup: "Create mocked Print-My-Barcode service that returns HTTP 503 for each call"
        def restServiceStub = Stub(RestService)

        def responseStatus = 503
        def responseJSON = [
            "general": [
                "Service Temporarily Unavailable"
            ]
        ]
        def responseMap = [
            response: [status: responseStatus],
            reader: responseJSON
        ]

        def expected_error_message = "The request was not successful. The server responded with 503 code. The error message is: $responseJSON".toString()

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def labelPrinter = new LabelPrinter()
        labelPrinter.restService = restServiceStub

        when:
        labelPrinter.printLabel()

        then:
        labelPrinter.responseCode == responseStatus
        RestServiceException ex = thrown()
        def matcher = ex.message =~ /(?s).*$expected_error_message.*/
        assert matcher.matchesPartially()
    }

    def "when sending a correct label for printing with Print-My-Barcode service, then got beck 201 HTTP code"() {

        setup: "Create mocked Print-My-Barcode service for printing a label"
        def restServiceStub = Stub(RestService)

        def label_template_id = 1
        def printer_name = "d304bc"
        def labels_json = [
            "header": [
                "header_text_1": "header_text_1",
                "header_text_2": "header_text_2"
            ],
            "footer": [
                "footer_text_1": "footer_text_1",
                "footer_text_2": "footer_text_2"
            ],
            "body": [
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ],
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ]
            ]
        ]

        Map<?, ?> requestHeaders = [:]
        String printLabelPath = LabelPrinter.printLabelPath()
        def requestBody = [
            "data": [
                "attributes": [
                    "printer_name": printer_name,
                    "label_template_id": label_template_id,
                    "labels": labels_json
                ]
            ]
        ]

        def responseStatus = 201
        def responseBody = [
            data: [
                "attributes": [
                    "printer_name": printer_name,
                    "label_template_id": label_template_id,
                    "labels": labels_json
                ]
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseBody
        ]

        restServiceStub.request(Method.POST, requestHeaders, JSON, printLabelPath, requestBody) >> responseMap

        def labelPrinter = new LabelPrinter()
        labelPrinter.restService = restServiceStub

        expect: "Check if we get back a valid response"
        labelPrinter.printLabel(requestBody)
        labelPrinter.responseCode == responseStatus
    }

    def "when sending a label with non existing printer name for printing with Print-My-Barcode service, then got beck 422 HTTP code"() {

        setup: "Create mocked Print-My-Barcode service for printing a label"
        def restServiceStub = Stub(RestService)

        def label_template_id = 1
        def printer_name = "non_existing_printer"
        def labels_json = [
            "header": [
                "header_text_1": "header_text_1",
                "header_text_2": "header_text_2"
            ],
            "footer": [
                "footer_text_1": "footer_text_1",
                "footer_text_2": "footer_text_2"
            ],
            "body": [
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ],
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ]
            ]
        ]

        Map<?, ?> requestHeaders = [:]
        String printLabelPath = LabelPrinter.printLabelPath()
        def requestBody = [
            "data": [
                "attributes": [
                    "printer_name": printer_name,
                    "label_template_id": label_template_id,
                    "labels": labels_json
                ]
            ]
        ]

        def responseStatus = 422
        def expected_error_message = "Printer does not exist"
        def responseBody = [
            "errors": [
                "printer": [
                    expected_error_message
                ]
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseBody
        ]

        restServiceStub.request(Method.POST, requestHeaders, JSON, printLabelPath, requestBody) >> responseMap

        def labelPrinter = new LabelPrinter()
        labelPrinter.restService = restServiceStub

        when:
        labelPrinter.printLabel(requestBody)

        then: "Check if we got back a failed response"
        labelPrinter.responseCode == responseStatus

        RestServiceException ex = thrown()
        def matcherEx = ex.message =~ /(?s).*$expected_error_message.*/
        assert matcherEx.matchesPartially()
    }

    def "when sending a label with non existing label template id for printing with Print-My-Barcode service, then got beck 422 HTTP code"() {

        setup: "Create mocked Print-My-Barcode service for printing a label"
        def restServiceStub = Stub(RestService)

        def label_template_id = 9999
        def printer_name = "abc123"
        def labels_json = [
            "header": [
                "header_text_1": "header_text_1",
                "header_text_2": "header_text_2"
            ],
            "footer": [
                "footer_text_1": "footer_text_1",
                "footer_text_2": "footer_text_2"
            ],
            "body": [
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ],
                [
                    "location": [
                        "location": "location",
                        "parent_location": "parent_location",
                        "barcode": "barcode"
                    ]
                ]
            ]
        ]

        Map<?, ?> requestHeaders = [:]
        String printLabelPath = LabelPrinter.printLabelPath()
        def requestBody = [
            "data": [
                "attributes": [
                    "printer_name": printer_name,
                    "label_template_id": label_template_id,
                    "labels": labels_json
                ]
            ]
        ]

        def responseStatus = 422
        def expected_error_message = "Label template does not exist"
        def responseBody = [
            "errors": [
                "label_template": [
                    expected_error_message
                ]
            ]
        ]
        def responseMap = [
            response: [status: responseStatus],
            reader: responseBody
        ]

        restServiceStub.request(Method.POST, requestHeaders, JSON, printLabelPath, requestBody) >> responseMap

        def labelPrinter = new LabelPrinter()
        labelPrinter.restService = restServiceStub

        when:
        labelPrinter.printLabel(requestBody)

        then: "Check if we got back a failed response"
        labelPrinter.responseCode == responseStatus

        RestServiceException ex = thrown()
        def matcherEx = ex.message =~ /(?s).*$expected_error_message.*/
        assert matcherEx.matchesPartially()
    }

    def "when sending a blank label for printing with Print-My-Barcode service, then got beck 422 HTTP code"() {

        setup: "Create mocked Print-My-Barcode service for printing a label"
        def restServiceStub = Stub(RestService)

        def label_template_id = 1
        def printer_name = "abc123"
        def labels_json = []

        Map<?, ?> requestHeaders = [:]
        String printLabelPath = LabelPrinter.printLabelPath()
        def requestBody = [
            "data": [
                "attributes": [
                    "printer_name": printer_name,
                    "label_template_id": label_template_id,
                    "labels": labels_json
                ]
            ]
        ]

        def responseStatus = 422
        def expected_error_message = [ "can't be blank","There should be some labels" ]
        def responseBody = [
            "errors": [
                "labels": expected_error_message
            ]
        ]
        def responseMap = [
            response: [status: responseStatus],
            reader: responseBody
        ]

        restServiceStub.request(Method.POST, requestHeaders, JSON, printLabelPath, requestBody) >> responseMap

        def labelPrinter = new LabelPrinter()
        labelPrinter.restService = restServiceStub

        when:
        labelPrinter.printLabel(requestBody)

        then: "Check if we got back a failed response"
        labelPrinter.responseCode == responseStatus

        RestServiceException ex = thrown()
        def matcherEx = ex.message =~ /(?s).*$expected_error_message.*/
        assert matcherEx.matchesPartially()
    }
}
