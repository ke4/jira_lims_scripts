package uk.ac.sanger.scgcf.jira.lims.scripts.validators

import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper
import uk.ac.sanger.scgcf.jira.lims.validations.SequencescapeValidator

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue (from binding)
Issue curIssue = issue

LOG.debug "Validating Sequencescape Project Name existence"

def sequencescapeValidator = new SequencescapeValidator()

// get the project and study name from the issue
String projectName = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCFName("PROJECT_NAME"))
LOG.debug "The retrieved project name: '$projectName'"

String studyName = JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCFName("STUDY_NAME"))
LOG.debug "The retrieved study name: '$studyName'"

def invalidInputException = new InvalidInputException()
if (!sequencescapeValidator.validateProjectName(projectName)) {
    invalidInputException.addError(
            JiraAPIWrapper.getCustomFieldIDByName(ConfigReader.getCFName("PROJECT_NAME")),
            SequencescapeValidator.SS_PROJECT_NOT_EXISTS_ERROR_MESSAGE)
}

if (!sequencescapeValidator.validateStudyName(studyName)) {
    invalidInputException.addError(
            JiraAPIWrapper.getCustomFieldIDByName(ConfigReader.getCFName("STUDY_NAME")),
            SequencescapeValidator.SS_STUDY_NOT_EXISTS_ERROR_MESSAGE)
}

if (invalidInputException.getErrors().size() > 0) {
    throw invalidInputException
}