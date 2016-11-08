package uk.ac.sanger.scgcf.jira.lims.scripts.validators

import com.atlassian.jira.issue.Issue
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.validations.DependentFieldValidator
import uk.ac.sanger.scgcf.jira.lims.validations.MandatoryFieldValidator
import uk.ac.sanger.scgcf.jira.lims.validations.WorkflowUtils

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

// get the current issue and transient variables (from binding)
Issue curIssue = issue
Map<String, Object> curTansientVars = transientVars

LOG.debug "Validating Sequencescape Project Name existence"

def mainConfigKey = "validation"
def validationType = "dependentFields"
def projectName = curIssue.getProjectObject().getName()
def issueTypeName = curIssue.getIssueType().getName()
def transitionName = WorkflowUtils.getTransitionName(curIssue, curTansientVars)

def elementsToValidate = ConfigReader.getConfigElement([mainConfigKey, validationType, projectName, issueTypeName, transitionName])

LOG.debug "Dependent fields for $projectName[transition: $transitionName]:"

def dependentValidator = new DependentFieldValidator()

for (Map<String, String> elementToValidate : elementsToValidate ) {
    LOG.debug elementToValidate.get("parentFieldAlias") as String

    dependentValidator.validate(
            curIssue,
            elementToValidate.get("parentFieldAlias"),
            elementToValidate.get("parentFieldValue"),
            elementToValidate.get("dependentFieldAlias")
    )
}
