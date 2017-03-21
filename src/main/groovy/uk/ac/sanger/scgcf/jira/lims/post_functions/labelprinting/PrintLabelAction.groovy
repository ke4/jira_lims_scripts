package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting

import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.post_functions.IssueAction
import uk.ac.sanger.scgcf.jira.lims.services.LabelPrinter

/**
 * This post function prints label(s) with the given label printer.
 *
 * Created by ke4 on 17/03/2017.
 */
@Slf4j(value = "LOG")
class PrintLabelAction implements IssueAction {

    String printerName
    int numberOfLabels
    LabelTemplates labelTemplate
    def labelData

    /**
     * Constructor for {@code PrintLabelAction}.
     *
     * @param printerName the name of the printer to print
     * @param numberOfLabels the number of label to print
     * @param labelTemplate the template to use to print the label(s)
     * @param labelData contains the data to print on the label
     */
    PrintLabelAction(String printerName, int numberOfLabels, LabelTemplates labelTemplate, def labelData) {
        this.printerName = printerName
        this.numberOfLabels = numberOfLabels
        this.labelTemplate = labelTemplate
        this.labelData = labelData
    }

    @Override
    public void execute() {
        PlateLabelGenerator labelGenerator = new PlateLabelGenerator(printerName, numberOfLabels, labelTemplate, labelData)
        def labelToPrint = labelGenerator.createLabel()

        LOG.debug("Label to print: ${labelToPrint.toString()}")

        LabelPrinter labelPrinter = new LabelPrinter()
        labelPrinter.printLabel(labelToPrint)
    }
}
