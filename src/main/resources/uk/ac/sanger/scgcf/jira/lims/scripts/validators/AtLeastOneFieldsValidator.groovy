package uk.ac.sanger.scgcf.jira.lims.scripts.validators

import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.validations.AtLeastOneFieldValidator
import uk.ac.sanger.scgcf.jira.lims.validations.MandatoryFieldValidator
import uk.ac.sanger.scgcf.jira.lims.validations.WorkflowUtils

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue and transient variables (from binding)
Issue curIssue = issue
Map<String, Object> curTansientVars = transientVars

LOG.debug "Executing 'At least one fields' validator"

def mainConfigKey = "validation"
def validationType = "atLeastOneFields"
def projectName = curIssue.getProjectObject().getName()
def issueTypeName = curIssue.getIssueType().getName()
def transitionName = WorkflowUtils.getTransitionName(curIssue, curTansientVars)

List<List<String>> atLeastOneFieldAliasNames = ConfigReader.getConfigElement([mainConfigKey, validationType, projectName, issueTypeName, transitionName])

LOG.debug "'at least one fields' for $projectName[transition: $transitionName]:"

def atLeastOneFieldValidator = new AtLeastOneFieldValidator()

atLeastOneFieldAliasNames.each {
    LOG.debug it as String
    atLeastOneFieldValidator.validate(curIssue, it)
}
