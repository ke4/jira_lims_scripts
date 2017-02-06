package uk.ac.sanger.scgcf.jira.lims.post_functions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.enums.WorkflowName
import uk.ac.sanger.scgcf.jira.lims.utils.PlateActionParameterHolder
import uk.ac.sanger.scgcf.jira.lims.utils.PlateAdderParametersCreator
import uk.ac.sanger.scgcf.jira.lims.utils.ValidatorExceptionHandler
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

/**
 * This post function extracts a list of selected plates from an nFeed custom field and adds them
 * to the current issue via a function in {@code WorkflowUtils}.
 * It adds a link and transition the plate ticket state if appropriate.
 *
 * Created by ke4 on 24/01/2017.
 */
@Slf4j(value = "LOG")
class PlateAdder {

    Map<String, PlateActionParameterHolder> plateActionParameterHolders
    Issue curIssue
    String workflowName
    String customFieldName

    public PlateAdder(Issue curIssue, String workflowName, String customFieldName) {
        this.curIssue = curIssue
        this.workflowName = workflowName
        this.customFieldName = customFieldName

        initPlateActionParameterHolders()
    }

    public void execute() {
        if (!(curIssue != null && workflowName != null && customFieldName != null)) {
            InvalidInputException invalidInputException =
                    new InvalidInputException("The passed arguments are invalid."
                        + "[curIssue: $curIssue, workflowName: $workflowName, customFieldName: $customFieldName]")
            ValidatorExceptionHandler.throwAndLog(invalidInputException, invalidInputException.message, null)
        }

        LOG.debug "Post-function for adding plates to $workflowName workflow".toString()

        // fetch the list of selected plates from the nFeed custom field
        def customFieldManager = ComponentAccessor.getCustomFieldManager()
        def customField = customFieldManager.getCustomFieldObject(ConfigReader.getCFId(customFieldName))

        if(customField != null) {
            // the value of the nFeed field is an array of long issue ids for the selected plates
            String[] arrayPlateIds = curIssue.getCustomFieldValue(customField)

            arrayPlateIds.each { LOG.debug "Plate ID: $it has been selected to add" }

            // if user hasn't selected anything do nothing further
            if (arrayPlateIds == null) {
                LOG.debug("No plates selected, nothing to do")
                return
            }

            PlateActionParameterHolder parameters = plateActionParameterHolders.get(workflowName)
            parameters.plateIds = arrayPlateIds

            // link and transition the plate issue(s)
            WorkflowUtils.addPlatesToGivenWorkFlow(parameters)

        } else {
            LOG.error("Failed to get the plate array custom field for adding plates")
        }
    }

    private void initPlateActionParameterHolders() {
        plateActionParameterHolders = new HashMap<>()
        plateActionParameterHolders.put(WorkflowName.IMD.toString(),
                PlateAdderParametersCreator.getIMDParameters(curIssue))
        plateActionParameterHolders.put(WorkflowName.SUBMISSION.toString(),
                PlateAdderParametersCreator.getSubmissionParameters(curIssue))

    }
}