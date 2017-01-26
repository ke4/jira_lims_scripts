package uk.ac.sanger.scgcf.jira.lims.post_functions

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
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

        // get the transition action id
        int actionId = ConfigReader.getTransitionActionId("Plate SS2", "START_SUBMISSION")

        // for each issue in list link it to this issue
        arrayPlateIds.each { String plateIdString ->
            Long plateIdLong = Long.parseLong(plateIdString)
            LOG.debug("Attempting to link plate with ID ${plateIdString} to Submission with ID ${submIssue.id}".toString())
            MutableIssue mutableIssue = WorkflowUtils.getMutableIssueForIssueId(plateIdLong)

            if(mutableIssue != null && mutableIssue.getIssueType().getName() == 'Plate SS2') {

                // link the issues together
                WorkflowUtils.createIssueLink(submIssue, mutableIssue, 'Group includes')

                // transition the issue to 'In Submission' if it is 'Rdy for Submission'
                if(mutableIssue.getStatus().getName() == 'PltSS2 Rdy for Submission') {
                    WorkflowUtils.transitionIssue(mutableIssue, actionId)
                }
            }
        }
    }
}
