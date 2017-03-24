package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.templates

import spock.lang.Specification

import java.time.LocalDate
import java.time.Month

/**
 * Specifications for creating tube label(s) for Library Pool request
 *
 * Created by ke4 on 16/03/2017.
 */
class LibraryPoolTubeLabelTemplateSpec extends Specification {

    def "when instantiating with a barcode, then returns the correct label JSON structure"() {

        setup: "Create LibraryPoolTubeLabelJsonCreator"
        String infoText = "LPL"
        String fromParentText = "from parent -"
        List<String> numbers1 = (1..3).collect { number -> "0000000$number" }
        List<String> barcodes_batch1 = numbers1.collect { number -> "ABCD-$infoText-$number" }

        List<String> numbers2 = (4..7).collect { number -> "0000000$number" }
        List<String> barcodes_batch2 = numbers2.collect { number -> "ABCD-$infoText-$number" }

        def today = LocalDate.of(2017, Month.MARCH, 20).toString()
        String parentBarcode1 = "TEST-PAR-00000001"
        String parentBarcode2 = "TEST-PAR-00000002"
        def labelData = [
                'barcodes': [
                        "$parentBarcode1": barcodes_batch1,
                        "$parentBarcode2": barcodes_batch2
                ],
                'today': today
        ]
        def template = new LibraryPoolTubeLabelJsonCreator(labelData)
        template.today = LocalDate.of(2017, Month.MARCH, 20).toString()
        template.infoText = infoText

        def expectedMessage = [
            [
                "main_label": [
                    "first_text": barcodes_batch1[0],
                    "second_text": fromParentText,
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
                    "second_text": fromParentText,
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
                    "first_text": barcodes_batch1[2],
                    "second_text": fromParentText,
                    "third_text": parentBarcode1,
                    "fourth_text": today,
                    "fifth_text": "Q-3",
                    "label_barcode": barcodes_batch1[2],
                    "lid_btm_text": "Q-3",
                    "lid_middle_text": numbers1[2],
                    "lid_top_text": infoText
                ]
            ],
            [
                "main_label": [
                    "first_text": barcodes_batch2[0],
                    "second_text": fromParentText,
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
                    "second_text": fromParentText,
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
                    "second_text": fromParentText,
                    "third_text": parentBarcode2,
                    "fourth_text": today,
                    "fifth_text": "Q-3",
                    "label_barcode": barcodes_batch2[2],
                    "lid_btm_text": "Q-3",
                    "lid_middle_text": numbers2[2],
                    "lid_top_text": infoText
                ]
            ],
            [
                "main_label": [
                    "first_text": barcodes_batch2[3],
                    "second_text": fromParentText,
                    "third_text": parentBarcode2,
                    "fourth_text": today,
                    "fifth_text": "Q-4",
                    "label_barcode": barcodes_batch2[3],
                    "lid_btm_text": "Q-4",
                    "lid_middle_text": numbers2[3],
                    "lid_top_text": infoText
                ]
            ],

        ]

        expect:
        template.createLabelBody() == expectedMessage
    }
}
