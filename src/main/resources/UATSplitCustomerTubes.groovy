import com.atlassian.jira.issue.Issue

/**
 * Created by as28 on 14/07/16.
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
            case "UAT Customer Tubes Created":
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
    log.error "UAT Processing: Split Customer Tubes"
}