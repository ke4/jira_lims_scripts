package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.templates

import groovy.util.logging.Slf4j

/**
 * Label template creator for tube(s) for a Library Pool request.
 *
 * Created by ke4 on 22/03/2017.
 */
@Slf4j(value="LOG")
class LibraryPoolTubeLabelJsonCreator implements LabelJsonCreator {

    def barcodes
    String infoText
    String today

    LibraryPoolTubeLabelJsonCreator(def labelData) {
        LOG.debug("LibraryPoolTubeLabelJsonCreator labeldata: $labelData")
        this.barcodes = labelData.barcodes
        this.infoText = labelData.infoText
        this.today = labelData.today
    }

    @Override
    def createLabelBody() {
        List body = []
        int index = 0
        barcodes.each { parentBarcode, barcodes ->
            barcodes.each { barcode ->
                def labelJson = labelJson()
                index++
                String numberText = barcode.substring(9)

                labelJson.main_label.first_text = barcode
                labelJson.main_label.third_text = parentBarcode
                labelJson.main_label.fourth_text = today
                labelJson.main_label.fifth_text = "Q-$index"
                labelJson.main_label.label_barcode = barcode
                labelJson.main_label.lid_top_text = infoText
                labelJson.main_label.lid_middle_text = numberText
                labelJson.main_label.lid_btm_text = "Q-$index"

                body.add(labelJson)
            }
            index = 0
        }

        LOG.debug("Created label body: $body")

        body
    }

    @Override
    def labelJson() {
        [
            "main_label": [
                "first_text": "",
                "second_text": "from parent -",
                "third_text": "",
                "fourth_text": "",
                "fifth_text": "",
                "label_barcode": "",
                "lid_btm_text": "",
                "lid_middle_text": "",
                "lid_top_text": ""
            ]
        ]
    }
}
