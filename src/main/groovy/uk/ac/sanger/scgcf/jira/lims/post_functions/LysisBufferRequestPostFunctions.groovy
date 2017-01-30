package uk.ac.sanger.scgcf.jira.lims.post_functions

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.enums.IssueTypeName
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

/**
 * The {@code LysisBufferRequestPostFunctions} class holds post functions for the Lysis Buffer Requests project
 *
 * Created by as28 on 09/12/2016.
 */

@Slf4j(value = "LOG")
class LysisBufferRequestPostFunctions {

/**
 * Link a list of plates to the specified Submission issue
 *
 * @param arrayReagentIds the list of reagent issue ids
 * @param lbrIssue the Submission issue
 */
    public static void linkReagentsToRequest(String[] arrayReagentIds, Issue lbrIssue) {

        // for each issue in list link it to this issue
        arrayReagentIds.each { String reagentIdString ->
            Long reagentIdLong = Long.parseLong(reagentIdString)
            LOG.debug("Attempting to link reagent with ID ${reagentIdString} to LBR with ID ${lbrIssue.id}".toString())
            MutableIssue reagentMutableIssue = WorkflowUtils.getMutableIssueForIssueId(reagentIdLong)

            if(reagentMutableIssue != null && reagentMutableIssue.getIssueType().getName() == IssueTypeName.REAGENT_LOT_OR_BATCH.toString()) {

                // link the issues together
                LOG.debug("Calling link function in WorkflowUtils to link reagent to LBR")
                try {
                    WorkflowUtils.createIssueLink(lbrIssue, reagentMutableIssue, 'Uses Reagent')
                    LOG.debug("Successfully linked reagent with ID ${reagentIdString} to LBR with ID ${lbrIssue.id}".toString())
                } catch (Exception e) {
                    LOG.error("Failed to link reagent with ID ${reagentIdString} to LBR with ID ${lbrIssue.id}".toString())
                    LOG.error(e.message)
                }
            } else {
                LOG.error("Reagent issue null or unexpected issue type when linking reagent with ID ${reagentIdString} to LBR with ID ${lbrIssue.id}".toString())
            }
        }
    }
}
