package uk.ac.sanger.scgcf.jira.lims.scripts.uat

import com.atlassian.jira.issue.Issue
import uk.ac.sanger.scgcf.jira.lims.actions.UATFunctions
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

// check the issue type and state match those expected, if not return error
if (curIssue == null) {
    // TODO: error handling
    LOG.error "No current issue found, cannot continue"
    return
}

// check the issue type and status are as expected
def issueTypeName = curIssue.getIssueType().getName()
def issueStatusName = curIssue.getStatus().getName()

switch (issueTypeName) {
    case "Task":
        switch (issueStatusName) {
            case "UAT Cherry Picked Plate Created":
                process( curIssue )
                break
            default:
                // TODO: error handling
                LOG.error "Unrecognised status name ${issueStatusName}"
                break
        }
        break
    default:
        // TODO: error handling
        LOG.error "Unrecognised issue type name ${issueTypeName}"
        break
}

void process( Issue curIssue ) {
    LOG.debug "UAT Processing: Cherry Pick Plates"

    // get the source plate barcode
    String plateBarcode = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CMB_PLT_BARCODE"))
    LOG.debug "plateBarcode = ${plateBarcode}"

    // send to UATFunction and return plate barcode and details
    String chryPickPlateBarcode, chryPickPlateDetails
    (chryPickPlateBarcode, chryPickPlateDetails) = UATFunctions.cherryPickPlates(plateBarcode)

    // set the barcodes custom field
    JiraAPIWrapper.setCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CHRY_PLT_BARCODE"), chryPickPlateBarcode)

    // set the details custom field
    JiraAPIWrapper.setCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CHRY_PLT_DETAILS"), chryPickPlateDetails)
}