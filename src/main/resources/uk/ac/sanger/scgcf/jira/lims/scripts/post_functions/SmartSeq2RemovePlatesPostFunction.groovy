package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

/**
 * This post function extracts a list of selected plates from an nFeed custom field and removes them
 * from the current Smart-seq2 issue via a function in {@code WorkflowUtils}.
 * This removes the link and reverts the plate ticket state if appropriate.
 *
 * Created by ke4 on 24/01/2017.
 */

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

String workflowName = "Smart-seq2"

LOG.debug "Post-function for removing plates from a $workflowName workflow".toString()

// fetch the list of selected plates from the nFeed custom field
def customFieldMngr = ComponentAccessor.getCustomFieldManager()
def customField =  customFieldMngr.getCustomFieldObject(ConfigReader.getCFId('GENERIC_REMOVE_PLATES'))

if(customField != null) {
    // the value of the nFeed field is an array of long issue ids for the selected plates
    String[] arrayPlateIds = curIssue.getCustomFieldValue(customField)

    // if user hasn't selected anything do nothing further
    if (arrayPlateIds == null) {
        LOG.debug("No plates selected, nothing to do")
        return
    }

    // link and transition the plate issue(s)
    WorkflowUtils.removePlatesFromGivenWorkflow(arrayPlateIds, curIssue, "Smart-seq2",
            "REVERT_TO_READY_FOR_SS2", "PltSS2 In SS2")

} else {
    LOG.error("Failed to get the plate array custom field for removing plates")
}