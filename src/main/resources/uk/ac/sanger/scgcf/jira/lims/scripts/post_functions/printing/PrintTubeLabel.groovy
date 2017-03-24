package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions.printing

import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.LabelGenerator
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.LabelTemplates
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.PlateLabelGenerator
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.PrintLabelAction
import uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.TubeLabelGenerator
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper

/**
 * This script sends tube label(s) for printing to the given label printer.
 *
 * Created by ke4 on 24/03/2017.
 */

@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

Issue curIssue = issue

LOG.debug "Printing plate's barcode label(s)"

String printerName = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCustomFieldName("PRINTER_FOR_PLATE_LABELS"))
def labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER
def labelData = [:]

// @TODO the labelData structure should look like this:
//def labelData = [
//    'barcodes': [
//            <parentBarcode1>: [
//                "plexity": 2,
//                "barcodes": [] // not mandatory, but it could contain the already created barcodes (re-print)
//            ],
//            <parentBarcode2>: [
//                "plexity": 3,
//                "barcodes": [] // not mandatory, but it could contain the already created barcodes (re-print)
//            ]
//    ]
//]
// You have to gather the above data from the linked source plate ticket(s)!!!!

LabelGenerator labelGenerator = new TubeLabelGenerator(printerName, labelTemplate, labelData)

PrintLabelAction printLabelAction =
        new PrintLabelAction(labelGenerator)
printLabelAction.execute()
