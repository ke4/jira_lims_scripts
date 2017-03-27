package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.MockCustomField
import com.atlassian.jira.mock.component.MockComponentWorker
import groovyx.net.http.Method
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.configurations.JiraLimsServices
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper
import uk.ac.sanger.scgcf.jira.lims.services.BarcodeGenerator
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import static groovyx.net.http.ContentType.JSON

/**
 * Created by ke4 on 17/03/2017.
 */
class PlateLabelGeneratorSpec extends Specification {

    @Shared barcodeGeneratorDetails
    @Shared Issue issueStub

    def setupSpec() {
        EnvVariableAccess.metaClass.static.getJiraLimsConfigFilePath = { "./src/test/resources/jira_lims_config.json" }
        barcodeGeneratorDetails = ConfigReader.getServiceDetails(JiraLimsServices.BARCODE_GENERATOR)
        new MockComponentWorker().init();
        issueStub = Stub(Issue)
    }

    def "when calling createLabel method then generates the proper label"() {

        setup:
        def label_template_id = 12

        GroovyMock(ConfigReader, global:true)
        ConfigReader.getCustomFieldName(_) >> ""
        ConfigReader.getServiceDetails(_) >> [:]
        ConfigReader.getLabeltemplateDetails(_) >> ["id": label_template_id, "barcodeInfo": "ABC"]

        GroovyMock(JiraAPIWrapper, global:true)
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, "GENERATED_BARCODES") >> ""

        String printerName = "abcd123"
        int numberOfLabels = 1
        LabelTemplates labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER
        def labelData = [:]
        def prefix = "ABCD"
        def info = "EFG"
        def number = 1
        def barcode = "$prefix-$info-0000000$number"

        def expectedLabel = [
            "data": [
                "attributes": [
                    "printer_name": printerName,
                    "label_template_id": label_template_id,
                    "labels": [
                        "body": [
                            [
                                label_1: [
                                    barcode: barcode,
                                    barcode_text: barcode
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]

        def restServiceStub = Stub(RestService)

        def responseStatus = 201
        def responseJSON = [
            "id": 123,
            "prefix": prefix,
            "info": info,
            "number": number,
            "fullBarcode": barcode
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def barcodeGeneratorStub = Stub(BarcodeGenerator)
        barcodeGeneratorStub.generateSingleBarcode(_, _) >> "$prefix-$info-0000000$number"
        barcodeGeneratorStub.restService = restServiceStub

        PlateLabelGenerator plateLabelGenerator =
                new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData, issueStub)
        plateLabelGenerator.barcodeGenerator = barcodeGeneratorStub

        expect:
        plateLabelGenerator.createLabel() == expectedLabel
    }

    def "when calling createLabel method for 4 plates then generates the proper label"() {

        setup:
        def label_template_id = 12

        GroovyMock(ConfigReader, global:true)
        ConfigReader.getCustomFieldName(_) >> ""
        ConfigReader.getServiceDetails(_) >> [:]
        ConfigReader.getLabeltemplateDetails(_) >> ["id": label_template_id, "barcodeInfo": "ABC"]

        GroovyMock(JiraAPIWrapper, global:true)
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, "GENERATED_BARCODES") >> ""


        String printerName = "abcd123"
        int numberOfLabels = 4
        LabelTemplates labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER
        def labelData = [:]
        def prefix = "ABCD"
        def info = "EFG"
        def numbers = (1..4)
        def barcodes = numbers.collect { number -> "$prefix-$info-0000000$number"}

        def expectedLabel = [
            "data": [
                "attributes": [
                    "printer_name": printerName,
                    "label_template_id": label_template_id,
                    "labels": [
                        "body":
                            barcodes.collect { barcode ->
                                [
                                    label_1: [
                                        barcode: barcode,
                                        barcode_text: barcode
                                    ]
                                ]
                            }

                    ]
                ]
            ]
        ]

        println "BARCODES:" + barcodes
        println "EXPECTED LABEL:" + expectedLabel

        def restServiceStub = Stub(RestService)

        def responseStatus = 201
        def responseJSON = [
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": 00000001,
                "fullBarcode": "ABCD-EFG-00000001"
            ],
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": 00000002,
                "fullBarcode": "ABCD-EFG-00000002"
            ],
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": 00000003,
                "fullBarcode": "ABCD-EFG-00000003"
            ],
            [
                "id": 123,
                "prefix": prefix,
                "info": info,
                "number": 00000004,
                "fullBarcode": "ABCD-EFG-00000004"
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def barcodeGeneratorStub = Stub(BarcodeGenerator)
        barcodeGeneratorStub.generateBatchBarcodes(_, _, _) >> barcodes
        barcodeGeneratorStub.restService = restServiceStub

        PlateLabelGenerator plateLabelGenerator =
                new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData, issueStub)
        plateLabelGenerator.barcodeGenerator = barcodeGeneratorStub

        expect:
        plateLabelGenerator.createLabel() == expectedLabel
    }

    def "when calling createLabel method for 4 plates and it is a reprint and we are passing wrong barcodes then fails"() {

        setup:
        GroovyMock(JiraAPIWrapper, global:true)

        String printerName = "abcd123"
        int numberOfLabels = 4
        LabelTemplates labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER

        def prefix = "ABCD"
        def info = "EFG"
        def numbers = (1..4)
        def barcodes = numbers.collect { number -> "$prefix-$info-0000000$number"}
        def labelData = [ "barcodes": ["NOT-EXI-12344321"] ]

        def expectedLabel = [
                "data": [
                        "attributes": [
                                "printer_name": printerName,
                                "label_template_id": 12,
                                "labels": [
                                        "body":
                                                barcodes.collect { barcode ->
                                                    [
                                                            label_1: [
                                                                    barcode: barcode,
                                                                    barcode_text: barcode
                                                            ]
                                                    ]
                                                }

                                ]
                        ]
                ]
        ]

        println "BARCODES:" + barcodes
        println "EXPECTED LABEL:" + expectedLabel

        def restServiceStub = Stub(RestService)

        def responseStatus = 201
        def responseJSON = [
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000001,
                        "fullBarcode": "ABCD-EFG-00000001"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000002,
                        "fullBarcode": "ABCD-EFG-00000002"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000003,
                        "fullBarcode": "ABCD-EFG-00000003"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000004,
                        "fullBarcode": "ABCD-EFG-00000004"
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        PlateLabelGenerator plateLabelGenerator =
                new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData, issueStub)
        plateLabelGenerator.barcodeGenerator = barcodeGenerator

        expect:
        plateLabelGenerator.createLabel() != expectedLabel
    }

    def "when calling createLabel method for 4 plates and it is a reprint then generates the proper label"() {

        setup:
        GroovyMock(JiraAPIWrapper, global:true)

        String printerName = "abcd123"
        int numberOfLabels = 4
        LabelTemplates labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER

        def prefix = "ABCD"
        def info = "EFG"
        def numbers = (1..4)
        def barcodes = numbers.collect { number -> "$prefix-$info-0000000$number"}
        def labelData = [ "barcodes": barcodes ]

        def expectedLabel = [
                "data": [
                        "attributes": [
                                "printer_name": printerName,
                                "label_template_id": 12,
                                "labels": [
                                        "body":
                                                barcodes.collect { barcode ->
                                                    [
                                                            label_1: [
                                                                    barcode: barcode,
                                                                    barcode_text: barcode
                                                            ]
                                                    ]
                                                }

                                ]
                        ]
                ]
        ]

        println "BARCODES:" + barcodes
        println "EXPECTED LABEL:" + expectedLabel

        def restServiceStub = Stub(RestService)

        def responseStatus = 201
        def responseJSON = [
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000001,
                        "fullBarcode": "ABCD-EFG-00000001"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000002,
                        "fullBarcode": "ABCD-EFG-00000002"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000003,
                        "fullBarcode": "ABCD-EFG-00000003"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": info,
                        "number": 00000004,
                        "fullBarcode": "ABCD-EFG-00000004"
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        PlateLabelGenerator plateLabelGenerator =
                new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData, issueStub)
        plateLabelGenerator.barcodeGenerator = barcodeGenerator

        expect:
        plateLabelGenerator.createLabel() == expectedLabel
    }
}
