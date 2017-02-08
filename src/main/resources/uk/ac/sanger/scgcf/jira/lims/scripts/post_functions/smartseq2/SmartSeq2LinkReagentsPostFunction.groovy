package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.transform.Field
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.post_functions.LysisBufferRequestPostFunctions

/**
 * This post function extracts a list of selected reagents from an nFeed custom field and links them
 * to the current Smart-seq2 issue via a function in {@code LysisBufferRequestPostFunctions}.
 *
 *
 * TODO: refactor it and make it generic
 *
 *
 * Created by as28 on 09/12/2016.
 */

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

LOG.debug "Post-function for adding reagents to a Lysis Buffer Request"

// fetch the array of selected reagents from the nFeed custom field
def customFieldMngr = ComponentAccessor.getCustomFieldManager()
def customField =  customFieldMngr.getCustomFieldObject(ConfigReader.getCFId('CURRENT_SMART-SEQ2_REAGENTS'))

if(customField != null) {
    // the value of the nFeed field is a list of long issue ids for the selected reagents
    String[] arrayReagentIds = curIssue.getCustomFieldValue(customField)

    // if user hasn't selected anything do nothing further
    if (arrayReagentIds == null) {
        LOG.debug("No reagents selected, nothing to do")
        return
    }

    // link and transition the plate issue(s)
    LysisBufferRequestPostFunctions.linkReagentsToRequest(arrayReagentIds, curIssue)

} else {
    LOG.error("Failed to get the reagent list custom field for adding reagents to a Lysis Buffer Request")
}