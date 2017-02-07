package uk.ac.sanger.scgcf.jira.lims.post_functions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.enums.WorkflowName
import uk.ac.sanger.scgcf.jira.lims.utils.PlateActionParameterHolder
import uk.ac.sanger.scgcf.jira.lims.utils.PlateRemoverParametersCreator
import uk.ac.sanger.scgcf.jira.lims.utils.ValidatorExceptionHandler
import uk.ac.sanger.scgcf.jira.lims.utils.WorkflowUtils

/**
 * This post function extracts a list of selected plates from an nFeed custom field and removes them
 * from the current issue via a function in {@code WorkflowUtils}.
 * It removes the link and reverts the plate ticket state if appropriate.
 *
 * Created by ke4 on 24/01/2017.
 */
@Slf4j(value = "LOG")
class PlateRemover {

    Map<String, PlateActionParameterHolder> plateRemovalParameterHolders
    Issue curIssue
    String workflowName
    String customFieldName
    String[] arrayPlateIds

    public PlateRemover(Issue curIssue, String workflowName, String customFieldName) {
        this.curIssue = curIssue
        this.workflowName = workflowName
        this.customFieldName = customFieldName

        initPlateRemovalParameterHolders()
    }

    public void execute() {
        if (!(curIssue != null && workflowName != null && customFieldName != null)) {
            InvalidInputException invalidInputException =
                    new InvalidInputException("The passed arguments are invalid."
                        + "[curIssue: $curIssue, workflowName: $workflowName, customFieldName: $customFieldName]")
            ValidatorExceptionHandler.throwAndLog(invalidInputException, invalidInputException.message, null)
        }

        LOG.debug "Post-function for removing plates from a $workflowName workflow".toString()

        // fetch the list of selected plates from the nFeed custom field
        def customFieldManager = ComponentAccessor.getCustomFieldManager()
        def customField = customFieldManager.getCustomFieldObject(ConfigReader.getCFId(customFieldName))

        if(customField != null) {
            // the value of the nFeed field is an array of long issue ids for the selected plates
            arrayPlateIds = curIssue.getCustomFieldValue(customField)

            arrayPlateIds.each { LOG.debug "Plate ID: $it has been selected to remove" }

            // if user hasn't selected anything do nothing further
            if (arrayPlateIds == null) {
                LOG.debug("No plates selected, nothing to do")
                return
            }

            PlateActionParameterHolder parameters = plateRemovalParameterHolders.get(workflowName)
            parameters.plateIds = arrayPlateIds

            // link and transition the plate issue(s)
            WorkflowUtils.removePlatesFromGivenGrouping(parameters)

        } else {
            LOG.error("Failed to get the plate array custom field for removing plates")
        }
    }

    private void initPlateRemovalParameterHolders() {
        plateRemovalParameterHolders = new HashMap<>()
        plateRemovalParameterHolders.put(WorkflowName.SMART_SEQ2.toString(),
                PlateRemoverParametersCreator.getSmartSeq2Parameters(curIssue))
        plateRemovalParameterHolders.put(WorkflowName.IMD.toString(),
                PlateRemoverParametersCreator.getIMDParameters(curIssue))
        plateRemovalParameterHolders.put(WorkflowName.SUBMISSION.toString(),
                PlateRemoverParametersCreator.getSubmissionParameters(curIssue))

    }
}