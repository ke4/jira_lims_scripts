import com.atlassian.jira.issue.Issue
import uk.ac.sanger.scgcf.jira.lims.actions.UATFunctions
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper

/**
 * Created by as28 on 05/07/16.
 */

// get the current issue (from binding)
Issue curIssue = issue

// check the issue type and state match those expected, if not return error
if (curIssue == null) {
    // TODO: error handling
    log.error "No current issue found, cannot continue"
    return
}

// check the issue type is as expected
def issueTypeName = curIssue.getIssueType().getName()

// NB. do not check state as this transition can be called from all other states

switch (issueTypeName) {
    case "Task":
        process( curIssue )
        break
    default:
        // TODO: error handling
        log.error "Unrecognised issue type name ${issueTypeName}"
        break
}

void process( Issue curIssue ) {
    log.error "UAT Processing: Reset All Fields for issue key = ${curIssue.getKey()}"

    // fetch field names using aliases
    def fieldNames = [
            ConfigReader.getCFName("UAT_CUST_TUBE_BARCODES"),
            ConfigReader.getCFName("UAT_CUST_TUBE_DETAILS"),
            ConfigReader.getCFName("UAT_SPLIT_PLT_BARCODES"),
            ConfigReader.getCFName("UAT_SPLIT_PLT_DETAILS"),
            ConfigReader.getCFName("UAT_CMB_PLT_BARCODE"),
            ConfigReader.getCFName("UAT_CMB_PLT_DETAILS"),
            ConfigReader.getCFName("UAT_CHRY_PLT_BARCODE"),
            ConfigReader.getCFName("UAT_CHRY_PLT_DETAILS"),
            ConfigReader.getCFName("UAT_STAMP_PLT_BARCODE"),
            ConfigReader.getCFName("UAT_STAMP_PLT_DETAILS"),
            ConfigReader.getCFName("UAT_SEL_STAMP_PLT_BARCODE"),
            ConfigReader.getCFName("UAT_SEL_STAMP_PLT_DETAILS"),
            ConfigReader.getCFName("UAT_POOL_TUBE_BARCODES"),
            ConfigReader.getCFName("UAT_POOL_TUBE_DETAILS"),
            ConfigReader.getCFName("UAT_NORM_TUBE_BARCODES"),
            ConfigReader.getCFName("UAT_NORM_TUBE_DETAILS")
    ]
    log.error fieldNames

    // clear the custom field value in the issue
    fieldNames.each { curFieldName ->
        log.error "curFieldName: ${curFieldName}"
        JiraAPIWrapper.clearCustomFieldValueByName( curIssue, curFieldName )
    }
}