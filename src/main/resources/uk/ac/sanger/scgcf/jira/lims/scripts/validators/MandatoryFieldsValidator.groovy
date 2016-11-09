package uk.ac.sanger.scgcf.jira.lims.scripts.validators

import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.validations.MandatoryFieldValidator
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue and transient variables (from binding)
Issue curIssue = issue
Map<String, Object> curTansientVars = transientVars

LOG.debug "Validating Sequencescape Project Name existence"

def mainConfigKey = "validation"
def validationType = "mandatoryFields"
def projectName = curIssue.getProjectObject().getName()
def issueTypeName = curIssue.getIssueType().getName()
def transitionName = WorkflowUtils.getTransitionName(curIssue, curTansientVars)

def mandatoryFieldNames = ConfigReader.getConfigElement([mainConfigKey, validationType, projectName, issueTypeName, transitionName])

LOG.debug "Mandatory fields for $projectName[transition: $transitionName]:"
LOG.debug mandatoryFieldNames as String

def mandatoryValidator = new MandatoryFieldValidator()

mandatoryValidator.validate(curIssue, mandatoryFieldNames)