package uk.ac.sanger.scgcf.jira.lims.post_functions.labelprinting

/**
 * Interface for creating label(s) to print with a label printer.
 *
 * All implemented class should implement the {@code createLabel} method, which returns a JSON
 * that is the representation of the labels and will be send to a label printer to print.
 *
 * Created by ke4 on 23/03/2017.
 */
interface LabelGenerator {

    public def createLabel()
}