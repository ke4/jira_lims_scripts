package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting.templates

/**
 * Interface for creating the body part of the label JSON for the various label templates.
 *
 * Created by ke4 on 16/03/2017.
 */
interface LabelJsonCreator {

    /**
     * Creates the body part of the label template
     * @return a JSON structure with the body part of the label template
     */
    def createLabelBody()

    /**
     * Creates a label part of the label template.
     * @return a JSON structure with a label part of the label template
     */
    def labelJson()
}