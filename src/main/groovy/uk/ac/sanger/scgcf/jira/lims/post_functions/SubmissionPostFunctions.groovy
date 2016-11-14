package uk.ac.sanger.scgcf.jira.lims.post_functions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.util.ErrorCollection
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

/**
 * The {@code SubmissionPostFunctions} class holds post functions for the Submissions project
 *
 * Created by as28 on 09/11/2016.
 */

@Slf4j(value = "LOG")
class SubmissionPostFunctions {

    /**
     * Link a list of plates to the specified Submission issue and transition them if appropriate.
     *
     * @param arrayPlateIds the list of plate issue ids
     * @param submIssue the Submission issue
     */
    public static void addPlatesToSubmission(String[] arrayPlateIds, Issue submIssue) {

        // for each issue in list link it to this issue
        arrayPlateIds.each { String plateIdString ->
            Long plateIdLong = Long.parseLong(plateIdString)
            LOG.debug("Attempting to link plate with ID ${plateIdString} to Submission with ID ${submIssue.id}".toString())

            def issMngr = ComponentAccessor.getIssueManager()

            def plateIssue = issMngr.getIssueObject(plateIdLong)
            def isPlateIssueValid = (plateIssue != null) && (plateIssue.getIssueType().getName() == 'Plate SS2')

            if(isPlateIssueValid) {

                // link the issues together
                WorkflowUtils.createIssueLink(submIssue, plateIssue, 'Group includes')

                // transition the issue to 'In Submission' if it is 'Rdy for Submission'
                if(plateIssue.getStatus().getName() == 'PltSS2 Rdy for Submission') {

                    // fetch action id for this transition from config file
                    def mainConfigKey = "transitions"
                    def workflowName = "plate_ss2"
                    def transitionAlias = "START_SUBMISSION"
                    int actionId = ConfigReader.getConfigElement([mainConfigKey, workflowName, transitionAlias, "tactionid"]) as int

                    // transition the issue to 'In Submission'
                    ErrorCollection ec = WorkflowUtils.transitionIssue(plateIssue, actionId)
                    if(ec != null) {
                        LOG.error("Error transitioning plate issue with ID ${plateIdString}".toString())
                        // Get all non field-specific error messages
                        Collection<String> stringErrors = ec.getErrorMessages()
                        stringErrors.eachWithIndex{ String err, int i ->
                            LOG.error("Error ${i}: ${err}".toString())
                        }
                    }
                }
            } else {
                LOG.error("Plate issue with ID ${plateIdString} is not valid when adding to Submission with ID ${submIssue.id}".toString())
            }
        }
    }

    /**
     * Remove the links between a list of plates and the specified Submission issue and transition them if appropriate.
     *
     * @param arrayPlateIds the list of plate issue ids
     * @param submIssue the Submission issue
     */
    public static void removePlatesFromSubmission(String[] arrayPlateIds, Issue submIssue) {

        // for each issue in list de-link it from the Submission issue and check if state should be changed
        arrayPlateIds.each { String plateIdString ->
            Long plateIdLong = Long.parseLong(plateIdString)
            LOG.debug("Removing link to plate with ID ${plateIdString} from Submission".toString())

            def issMngr = ComponentAccessor.getIssueManager()

            def plateIssue = issMngr.getIssueObject(plateIdLong)
            def isPlateIssueValid = (plateIssue != null) && (plateIssue.getIssueType().getName() == 'Plate SS2')

            if(isPlateIssueValid) {

                // remove the link between the two issues
                WorkflowUtils.removeIssueLink(submIssue, plateIssue, 'Group includes')

                // if state of plate was 'In Submission' transition the issue back to 'Rdy for Submission'
                if(plateIssue.getStatus().getName() == 'PltSS2 In Submission') {

                    def mainConfigKey = "transitions"
                    def workflowName = "plate_ss2"
                    def transitionAlias = "REVERT_TO_READY_FOR_SUBMISSION"
                    int actionId = ConfigReader.getConfigElement([mainConfigKey, workflowName, transitionAlias, "tactionid"]) as int

                    ErrorCollection ec = WorkflowUtils.transitionIssue(plateIssue, actionId)
                    if(ec != null) {
                        LOG.error("Error transitioning plate issue with ID ${plateIdString}".toString())
                        // Get all non field-specific error messages
                        Collection<String> stringErrors = ec.getErrorMessages()
                        stringErrors.eachWithIndex{ String err, int i ->
                            LOG.error("Error ${i}: ${err}".toString())
                        }
                    }
                }
            } else {
                LOG.error("Plate issue with ID ${plateIdString} is not valid when removing from Submission with ID ${submIssue.id}".toString())
            }
        }
    }
}
