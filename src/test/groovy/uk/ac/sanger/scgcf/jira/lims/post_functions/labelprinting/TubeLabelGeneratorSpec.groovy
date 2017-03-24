package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting

import groovyx.net.http.Method
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.configurations.JiraLimsServices
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.templates.LibraryPoolTubeLabelJsonCreator
import uk.ac.sanger.scgcf.jira.lims.services.BarcodeGenerator
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import java.time.LocalDate
import java.time.Month

import static groovyx.net.http.ContentType.JSON

/**
 * Specifications generating label templates for tube(s).
 * 
 * Created by ke4 on 23/03/2017.
 */
class TubeLabelGeneratorSpec extends Specification {

    @Shared barcodeGeneratorDetails

    def setupSpec() {
        EnvVariableAccess.metaClass.static.getJiraLimsConfigFilePath = { "./src/test/resources/jira_lims_config.json" }
        barcodeGeneratorDetails = ConfigReader.getServiceDetails(JiraLimsServices.BARCODE_GENERATOR)
    }

    def "when calling createLabel method for 3 plates for Library Pool request then generates the proper labels"() {

        setup:
        String printerName = "abcd123"
        LabelTemplates labelTemplate = LabelTemplates.LIBRARY_POOL_TUBE

        String parentBarcode1 = "TEST-PAR-00000001"
        String parentBarcode2 = "TEST-PAR-00000002"
        def prefix = "ABCD"
        def infoText = "EFG"

        def labelData = [
                'barcodes': [
                    "$parentBarcode1": [
                        "plexity": 2
                    ],
                    "$parentBarcode2": [
                        "plexity": 3
                    ]
                ]
        ]

        List<String> numbers1 = (1..2).collect { number -> "0000000$number" }
        List<String> barcodes_batch1 = numbers1.collect { number -> "ABCD-$infoText-$number" }
        List<String> numbers2 = (3..5).collect { number -> "0000000$number" }
        List<String> barcodes_batch2 = numbers2.collect { number -> "ABCD-$infoText-$number" }
        def today = LocalDate.of(2017, Month.MARCH, 20).toString()
        def expectedLabel = [
            "data": [
                "attributes": [
                    "printer_name": printerName,
                    "label_template_id": 104,
                    "labels": [
                        "body": [
                            [
                                "main_label": [
                                    "first_text": barcodes_batch1[0],
                                    "second_text": "from parent -",
                                    "third_text": parentBarcode1,
                                    "fourth_text": today,
                                    "fifth_text": "Q-1",
                                    "label_barcode": barcodes_batch1[0],
                                    "lid_btm_text": "Q-1",
                                    "lid_middle_text": numbers1[0],
                                    "lid_top_text": infoText
                                ]
                            ],
                            [
                                "main_label": [
                                    "first_text": barcodes_batch1[1],
                                    "second_text": "from parent -",
                                    "third_text": parentBarcode1,
                                    "fourth_text": today,
                                    "fifth_text": "Q-2",
                                    "label_barcode": barcodes_batch1[1],
                                    "lid_btm_text": "Q-2",
                                    "lid_middle_text": numbers1[1],
                                    "lid_top_text": infoText
                                ]
                            ],
                            [
                                "main_label": [
                                    "first_text": barcodes_batch2[0],
                                    "second_text": "from parent -",
                                    "third_text": parentBarcode2,
                                    "fourth_text": today,
                                    "fifth_text": "Q-1",
                                    "label_barcode": barcodes_batch2[0],
                                    "lid_btm_text": "Q-1",
                                    "lid_middle_text": numbers2[0],
                                    "lid_top_text": infoText
                                ]
                            ],
                            [
                                "main_label": [
                                    "first_text": barcodes_batch2[1],
                                    "second_text": "from parent -",
                                    "third_text": parentBarcode2,
                                    "fourth_text": today,
                                    "fifth_text": "Q-2",
                                    "label_barcode": barcodes_batch2[1],
                                    "lid_btm_text": "Q-2",
                                    "lid_middle_text": numbers2[1],
                                    "lid_top_text": infoText
                                ]
                            ],
                            [
                                "main_label": [
                                    "first_text": barcodes_batch2[2],
                                    "second_text": "from parent -",
                                    "third_text": parentBarcode2,
                                    "fourth_text": today,
                                    "fifth_text": "Q-3",
                                    "label_barcode": barcodes_batch2[2],
                                    "lid_btm_text": "Q-3",
                                    "lid_middle_text": numbers2[2],
                                    "lid_top_text": infoText
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
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": infoText,
                        "number": 00000001,
                        "fullBarcode": "ABCD-EFG-00000001"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": infoText,
                        "number": 00000002,
                        "fullBarcode": "ABCD-EFG-00000002"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": infoText,
                        "number": 00000003,
                        "fullBarcode": "ABCD-EFG-00000003"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": infoText,
                        "number": 00000004,
                        "fullBarcode": "ABCD-EFG-00000004"
                ],
                [
                        "id": 123,
                        "prefix": prefix,
                        "info": infoText,
                        "number": 00000005,
                        "fullBarcode": "ABCD-EFG-00000005"
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(Method.POST, _, JSON, _, _) >> responseMap

        def barcodeGenerator = new BarcodeGenerator()
        barcodeGenerator.restService = restServiceStub

        TubeLabelGenerator tubeLabelGenerator =
                new TubeLabelGenerator(printerName, labelTemplate, labelData)
        tubeLabelGenerator.barcodeGenerator = barcodeGenerator
        tubeLabelGenerator.today = today
        tubeLabelGenerator.barcodeInfo = infoText

        expect:
        tubeLabelGenerator.createLabel() == expectedLabel
    }}
