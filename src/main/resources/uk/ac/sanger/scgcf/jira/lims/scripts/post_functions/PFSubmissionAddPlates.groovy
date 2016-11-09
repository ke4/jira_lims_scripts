package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions

/**
 * Created by as28 on 04/11/2016.
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.ErrorCollection
import com.atlassian.jira.issue.Issue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.transform.Field
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

LOG.debug "Post-function for adding plates to a Submission"

def cFM = ComponentAccessor.getCustomFieldManager()
def customField =  cFM.getCustomFieldObject(ConfigReader.getCFId('ADD_PLATES_TO_SUBMISSION'))

if(customField != null) {
    def listPlateIds = curIssue.getCustomFieldValue(customField)
    if(listPlateIds == null) {
        return
    }

    // for each issue in list link it to this issue
    listPlateIds.each { String plateIdString ->
        Long plateIdLong = Long.parseLong(plateIdString)
        LOG.debug("Linking plate with ID ${plateIdString} to Submission")

        def issMngr = ComponentAccessor.getIssueManager()

        def plateIssue = issMngr.getIssueObject(plateIdLong)
        def isPlateIssueValid = (plateIssue != null) && (plateIssue.getIssueType().getName() == 'Plate SS2')

        if(isPlateIssueValid) {
            // link the issues together
            WorkflowUtils.createIssueLink(curIssue, plateIssue, 'Group includes')

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
                    // Get all non field-specific error messages
                    Collection<String> stringErrors = ec.getErrorMessages()
                    stringErrors.eachWithIndex{ String err, int i ->
                        LOG.error("Error ${i}: ${err}")
                    }
                }
            }
        } else {
            LOG.info("issue with ID " + plateIdString + " is not valid for linking")
        }
    }
}