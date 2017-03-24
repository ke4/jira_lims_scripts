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

    LabelGenerator labelGenerator

    /**
     * Constructor for {@code PrintLabelAction}.
     *
     * @param labelGenerator for generating label(s) to send to the label printer
     */
    PrintLabelAction(LabelGenerator labelGenerator) {
        this.labelGenerator = labelGenerator
    }

    @Override
    public void execute() {
        def labelToPrint = labelGenerator.createLabel()

        LOG.debug("Label to print: ${labelToPrint.toString()}")

        LabelPrinter labelPrinter = new LabelPrinter()
        labelPrinter.printLabel(labelToPrint)
    }
}
