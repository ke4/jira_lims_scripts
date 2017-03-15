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
 * Tests for calling the 'barcode-generator' service and get a barcode or
 * number of barcodes.
 * The input is the number of barcodes, prefix and info part of the barcode
 * and the response should be a String or a list of Strings.
 *
 * Created by ke4 on 08/03/2017.
 */
class BarcodeGeneratorTest extends Specification {

    @Shared barcodeGeneratorDetails

    def setupSpec() {
        EnvVariableAccess.metaClass.static.getJiraLimsConfigFilePath = { "./src/test/resources/jira_lims_config.json" }
        barcodeGeneratorDetails = ConfigReader.getServiceDetails(JiraLimsServices.BARCODE_GENERATOR)
    }

    def "When barcode generator service is down we should receive HTTP status 503"() {

        setup: "Create mocked barcode service that returns HTTP 503 for each call"
        def restServiceStub = Stub(RestService)
        def prefix = "ABCDE"
        def info = "info"

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

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        when:
        barcodeGenerator.generateSingleBarcode(prefix, info)

        then:
        barcodeGenerator.responseCode == 503
        RestServiceException ex = thrown()
        def matcher = ex.message =~ /(?s).*$expected_error_message.*/
        assert matcher.matchesPartially()
    }

    def "when call barcodes endpoint with a GET request, then we get a correct barcode"() {
        setup: "Create mocked barcode service that returns HTTP 200 and the current barcodes as a response"
        def restServiceStub = Stub(RestService)
        def prefix = "ABCD"
        def info = "IFO"
        def number = 999

        String generateSingleBarcodePath = "/${barcodeGeneratorDetails['getSingleBarcodePath']}"
        Map<?, ?> requestHeaders = [:]

        def responseStatus = 200
        def responseJSON = [
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": number,
                "fullBarcode": "ABCD-IFO-00000999"
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.GET, requestHeaders, JSON, generateSingleBarcodePath, null) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        expect: "Check if we get back a valid response"
        List<Map<String, String>> barcodes = barcodeGenerator.currentBarcodes
        barcodeGenerator.responseCode == 200
        def firstBarcode = barcodes.get(0)
        firstBarcode["fullBarcode"] == "ABCD-IFO-00000999"
    }

    def "when call barcodes endpoint with a POST request, then we get a numeric String value"() {

        setup: "Create mocked barcode service that returns HTTP 201 and a barcode response"
        def restServiceStub = Stub(RestService)
        def prefix = "ABCD"
        def info = "IFO"
        def number = 999
        def requestBody = [
                "info": info,
                "prefix": prefix
        ]
        String generateSingleBarcodePath = "/${barcodeGeneratorDetails['getSingleBarcodePath']}"
        Map<?, ?> requestHeaders = [:]


        def responseStatus = 201
        def responseJSON = [
            "id": 123,
            "prefix": prefix,
            "info": info,
            "number": number,
            "fullBarcode": "ABCD-IFO-00000999"
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, requestHeaders, JSON, generateSingleBarcodePath, requestBody) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        expect: "Check if we get back a valid barcode"
        barcodeGenerator.generateSingleBarcode(prefix, info) == "$prefix-$info-00000999".toString()
        barcodeGenerator.responseCode == 201
    }

    def "when call batch_barcodes endpoint with a POST request, then we get a List of numeric String values"() {

        setup: "Create mocked barcode service that returns a list of created barcodes"
        def restServiceStub = Stub(RestService)
        def prefix = "ABCD"
        def info = "IFO"
        List<Integer> number = [990, 991, 992, 993, 994]
        int numberOfBarcodes = number.size()
        def requestBody = [
                "info": info,
                "prefix": prefix,
                "numberOfBarcodes": numberOfBarcodes
        ]
        String generateBatchBarcodesPath = "/${barcodeGeneratorDetails['getBatchOfBarcodesPath']}"
        Map<?, ?> requestHeaders = [:]


        def responseStatus = 201
        def responseJSON = [
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": number.get(0),
                "fullBarcode": "$prefix-$info-00000${number.get(0)}"
            ],
            [
                "id": 124,
                "prefix": prefix,
                "info": info,
                "number": number.get(1),
                "fullBarcode": "$prefix-$info-00000${number.get(1)}"
            ],
            [
                "id": 125,
                "prefix": prefix,
                "info": info,
                "number": number.get(2),
                "fullBarcode": "$prefix-$info-00000${number.get(2)}"
            ],
            [
                "id": 126,
                "prefix": prefix,
                "info": info,
                "number": number.get(3),
                "fullBarcode": "$prefix-$info-00000${number.get(3)}"
            ],
            [
                "id": 127,
                "prefix": prefix,
                "info": info,
                "number": number.get(4),
                "fullBarcode": "$prefix-$info-00000${number.get(4)}"
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, requestHeaders, JSON, generateBatchBarcodesPath, requestBody) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        expect: "Check if we get back a valid barcode"
        List<String> barcodes = barcodeGenerator.generateBatchBarcodes(prefix, info, 5)
        barcodeGenerator.responseCode == 201
        barcodes.size() == numberOfBarcodes
        barcodes == number.collect { "$prefix-$info-00000$it".toString() }
    }
}
