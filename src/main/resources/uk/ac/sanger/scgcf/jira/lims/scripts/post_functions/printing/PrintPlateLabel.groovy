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
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper
import uk.ac.sanger.scgcf.jira.lims.services.BarcodeGenerator

/**
 * This script sends plate label(s) for printing to the given label printer.
 *
 * Created by ke4 on 15/03/2017.
 */

@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

Issue curIssue = issue

LOG.debug "Printing plate's barcode label(s)"

String printerName = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCustomFieldName("PRINTER_FOR_PLATE_LABELS"))
int numberOfLabels = Double.valueOf(JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCustomFieldName("NUMBER_OF_PLATES"))).intValue()
def labelTemplate = LabelTemplates.SS2_LYSIS_BUFFER
def labelData = [:]
String generatedBarcodesTxt = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCustomFieldName("GENERATED_BARCODES"))
if (generatedBarcodesTxt) {
    List<String> alreadyGeneratedBarcodes =
            Arrays.asList(generatedBarcodesTxt.split(','))

    if (alreadyGeneratedBarcodes.size() > 0) {
        labelData['barcodes'] = alreadyGeneratedBarcodes
    }
}

LabelGenerator labelGenerator = new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData, curIssue)
labelGenerator.barcodeGenerator = new BarcodeGenerator()

PrintLabelAction printLabelAction = new PrintLabelAction(labelGenerator)
printLabelAction.execute()
