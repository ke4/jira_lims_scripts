import com.atlassian.jira.issue.Issue
import uk.ac.sanger.scgcf.jira.lims.actions.UATFunctions
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper

/**
 * Created by as28 on 01/07/16.
 */

// get the current issue (from binding)
Issue curIssue = issue

// check the issue type and state match those expected, if not return error
if (curIssue == null) {
    // TODO: error handling
    log.error "No current issue found, cannot continue"
    return
}

// check the issue type and status are as expected
def issueTypeName = curIssue.getIssueType().getName()
def issueStatusName = curIssue.getStatus().getName()

switch (issueTypeName) {
    case "Task":
        switch (issueStatusName) {
            case "UAT Pending":
                process( curIssue )
                break
            default:
                // TODO: error handling
                log.error "Unrecognised status name ${issueStatusName}"
                break
        }
        break
    default:
        // TODO: error handling
        log.error "Unrecognised issue type name ${issueTypeName}"
        break
}

void process( Issue curIssue ) {
    log.error "UAT Processing: Create Customer Tubes"

//    // create the tubes and materials and return the barcodes and some details
//    String tubeBarcodes, tubeDetails
//    (tubeBarcodes, tubeDetails) = UATFunctions.createCustomerTubes()
//
//    // set the barcodes custom field
//    JiraAPIWrapper.setCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CUST_TUBE_BARCODES"), tubeBarcodes)
//
//    // set the details custom field
//    JiraAPIWrapper.setCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CUST_TUBE_DETAILS"), tubeDetails)
}
