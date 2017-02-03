package uk.ac.sanger.scgcf.jira.lims.scripts.post_functions.submission

import com.atlassian.jira.issue.Issue
import uk.ac.sanger.scgcf.jira.lims.enums.WorkflowName
import uk.ac.sanger.scgcf.jira.lims.post_functions.PlateRemover

Issue curIssue = issue

PlateRemover plateRemover = new PlateRemover(curIssue, WorkflowName.SUBMISSION.toString(), "GENERIC_REMOVE_PLATES")
plateRemover.execute()