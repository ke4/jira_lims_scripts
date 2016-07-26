package uk.ac.sanger.scgcf.jira.lims.service_wrappers

import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import groovy.util.logging.Slf4j
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField

/**
 * This class handles interactions with the Jira API
 *
 * N.B. The current issue is bound to the 'issue' variable by the scriptrunner environment
 * so you don't need to work it out.
 *
 * Created by as28 on 23/06/16.
 */

@Slf4j(value="LOG")
class JiraAPIWrapper {

    /**
     * Get a custom field object from its name
     * @param cfName
     * @return CustomField object
     */
    static CustomField getCustomFieldByName(String cfName) {
        LOG.debug "Custom field name: ${cfName}"
        def customFieldManager = ComponentAccessor.getCustomFieldManager()
        customFieldManager.getCustomFieldObjectByName(cfName)
    }

    /**
     * Get the value of a specified custom field for an issue
     * @param curIssue
     * @param cfName
     * @return String value of custom field
     * TODO: this needs to handle custom fields other than strings
     */
    static String getCustomFieldValueByName(Issue curIssue, String cfName) {
        LOG.debug "Custom field name: ${cfName}"
        String cfValue = getCustomFieldByName(cfName).getValue(curIssue) as String
        LOG.debug("CF value: ${cfValue}")
        cfValue
    }

    /**
     * Set the value of a specified custom field for an issue
     * @param curIssue
     * @param cfName
     * @param newValue
     * TODO: this needs to handle custom fields other than strings
     */
    static void setCustomFieldValueByName(Issue curIssue, String cfName, String newValue) {
        LOG.debug "setCustomFieldValueByName: Custom field name: ${cfName}"
        LOG.debug "setCustomFieldValueByName: New value: ${newValue}"

        // locate the custom field for the current issue
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
//        LOG.debug "class of customFieldManager = ${customFieldManager.getClass()}"
//        LOG.debug "list of objects:"
//        def cfmList = customFieldManager.getCustomFieldObjects(curIssue)
//        cfmList.eachWithIndex{ CustomField cf, int indx ->
//            LOG.debug "custom field name = ${cf.name}"
//        }
        def tgtField = customFieldManager.getCustomFieldObjects(curIssue).find {it.name == cfName}

        // update the value of the field and save the change in the database
        if (tgtField != null) {
            def changeHolder = new DefaultIssueChangeHolder()
            tgtField.updateValue(null, curIssue, new ModifiedValue(curIssue.getCustomFieldValue(tgtField), newValue),changeHolder)
        } else {
            LOG.error "setCustomFieldValueByName: Custom field with name <${cfName}> was not found, cannot set value"
        }
    }

    /**
     * Clear the value of a specified custom field for an issue
     * @param cfName
     * TODO: this needs to handle custom fields other than strings
     */
    static void clearCustomFieldValueByName(Issue curIssue, String cfName) {
        setCustomFieldValueByName(curIssue, cfName, null)
    }
}