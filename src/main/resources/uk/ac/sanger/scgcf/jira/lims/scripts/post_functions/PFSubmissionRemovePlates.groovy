package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions

/**
 * This post function extracts a list of selected plates from an nFeed custom field and removes them
 * from the current Submission issue.
 *
 * It removes the link and reverts the plate ticket state if appropriate.
 *
 * Created by as28 on 04/11/2016.
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.ErrorCollection
import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue and transient variables (from binding)
Issue curIssue = issue

LOG.debug "Post-function for removing plates from a Submission"

// fetch the nFeed custom field
def cFM = ComponentAccessor.getCustomFieldManager()
def customField =  cFM.getCustomFieldObject(ConfigReader.getCFId('REMOVE_PLATES_FROM_SUBMISSION'))

if(customField != null) {
    // the value of the nFeed field is a list of long issue ids for the selected plates
    def listPlateIds = curIssue.getCustomFieldValue(customField)
    if(listPlateIds == null) {
        return
    }

    // for each issue in list de-link it from the Submission issue and check if state should be changed
    listPlateIds.each { String plateIdString ->
        Long plateIdLong = Long.parseLong(plateIdString)
        LOG.debug("Removing link to plate with ID ${plateIdString} from Submission")

        def issMngr = ComponentAccessor.getIssueManager()

        def plateIssue = issMngr.getIssueObject(plateIdLong)
        def isPlateIssueValid = (plateIssue != null) && (plateIssue.getIssueType().getName() == 'Plate SS2')

        if(isPlateIssueValid) {

            // remove the link between the two issues
            WorkflowUtils.removeIssueLink(curIssue, plateIssue, 'Group includes')

            // if state of plate was 'In Submission' transition the issue back to 'Rdy for Submission'
            if(plateIssue.getStatus().getName() == 'PltSS2 In Submission') {

                def mainConfigKey = "transitions"
                def workflowName = "plate_ss2"
                def transitionAlias = "REVERT_TO_READY_FOR_SUBMISSION"
                int actionId = ConfigReader.getConfigElement([mainConfigKey, workflowName, transitionAlias, "tactionid"]) as int

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
            LOG.error("Removing plate issue with ID ${plateIdString} from Submission with ID ${curIssue.id} is not valid")
        }
    }
}