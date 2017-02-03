package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions.submission

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.transform.Field
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.post_functions.SubmissionPostFunctions

/**
 * This post function extracts a list of selected plates from an nFeed custom field and adds them
 * to the current Submission issue via a function in {@code SubmissionsPostFunctions}.
 * This links the issues and transitions the plate ticket state if appropriate.
 *
 * Created by as28 on 04/11/2016.
 */

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

LOG.debug "Post-function for adding plates to a Submission"

// fetch the array of selected plates from the nFeed custom field
def customFieldMngr = ComponentAccessor.getCustomFieldManager()
def customField =  customFieldMngr.getCustomFieldObject(ConfigReader.getCFId('ADD_PLATES_TO_SUBMISSION'))

if(customField != null) {
    // the value of the nFeed field is a list of long issue ids for the selected plates
    String[] arrayPlateIds = curIssue.getCustomFieldValue(customField)

    // if user hasn't selected anything do nothing further
    if (arrayPlateIds == null) {
        LOG.debug("No plates selected, nothing to do")
        return
    }

    // link and transition the plate issue(s)
    SubmissionPostFunctions.addPlatesToSubmission(arrayPlateIds, curIssue)

} else {
    LOG.error("Failed to get the plate array custom field for adding plates to a Submission")
}