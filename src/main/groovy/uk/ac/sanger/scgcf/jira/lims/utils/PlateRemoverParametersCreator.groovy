package uk.ac.sanger.scgcf.jira.lims.utils

import com.atlassian.jira.issue.Issue
import uk.ac.sanger.scgcf.jira.lims.enums.IssueLinkTypeName
import uk.ac.sanger.scgcf.jira.lims.enums.SS2PlateStateName
import uk.ac.sanger.scgcf.jira.lims.enums.TransitionName
import uk.ac.sanger.scgcf.jira.lims.enums.WorkflowName

/**
 * This class contains static factory method to create various {@code PlateRemovalParameterHolder} instances.
 *
 * Created by ke4 on 03/02/2017.
 */
class PlateRemoverParametersCreator {

    /**
     * Creates a {@code PlateRemovalParameterHolder} for removing a plate from the Smart-seq2 workflow
     *
     * @param curIssue the specific issue
     * @return PlateRemovalParameterHolder object holding all the parameters needed for removing a plate from the
     * Smart-seq2 workflow
     */
    public static PlateRemovalParameterHolder getSmartSeq2Parameters(Issue curIssue) {
        PlateRemovalParameterHolder removePlatesParams = getBasicPlateRemovalParameterHolder(curIssue)
        removePlatesParams.plateWorkflowName = WorkflowName.PLATE_SS2
        removePlatesParams.currentWorkflowName = WorkflowName.SMART_SEQ2
        removePlatesParams.transitionName = TransitionName.REVERT_TO_READY_FOR_SS2
        removePlatesParams.previousPlateState = SS2PlateStateName.PLATESS2_IN_SS2
        removePlatesParams.linkTypeName = IssueLinkTypeName.GROUP_INCLUDES

        removePlatesParams
    }

    /**
     * Creates a {@code PlateRemovalParameterHolder} for removing a plate from the IMD workflow
     *
     * @param curIssue the specific issue
     * @return PlateRemovalParameterHolder object holding all the parameters needed for removing a plate from the
     * IMD workflow
     */
    public static PlateRemovalParameterHolder getIMDParameters(Issue curIssue) {
        PlateRemovalParameterHolder removePlatesParams = getBasicPlateRemovalParameterHolder(curIssue)
        removePlatesParams.plateWorkflowName = WorkflowName.PLATE_SS2
        removePlatesParams.currentWorkflowName = WorkflowName.IMD
        removePlatesParams.transitionName = TransitionName.REVERT_TO_WITH_CUSTOMER
        removePlatesParams.previousPlateState = SS2PlateStateName.PLATESS2_IN_IMD
        removePlatesParams.linkTypeName = IssueLinkTypeName.GROUP_INCLUDES

        removePlatesParams
    }

    /**
     * Creates a {@code PlateRemovalParameterHolder} for removing a plate from the Submission workflow
     *
     * @param curIssue the specific issue
     * @return PlateRemovalParameterHolder object holding all the parameters needed for removing a plate from the
     * Submission workflow
     */
    public static PlateRemovalParameterHolder getSubmissionParameters(Issue curIssue) {
        PlateRemovalParameterHolder removePlatesParams = getBasicPlateRemovalParameterHolder(curIssue)
        removePlatesParams.plateWorkflowName = WorkflowName.PLATE_SS2
        removePlatesParams.currentWorkflowName = WorkflowName.SUBMISSION
        removePlatesParams.transitionName = TransitionName.REVERT_TO_READY_FOR_SUBMISSION
        removePlatesParams.previousPlateState = SS2PlateStateName.PLATESS2_IN_SUBMISSION
        removePlatesParams.linkTypeName = IssueLinkTypeName.GROUP_INCLUDES

        removePlatesParams
    }

    private static PlateRemovalParameterHolder getBasicPlateRemovalParameterHolder(Issue curIssue) {
        PlateRemovalParameterHolder removePlatesParams = new PlateRemovalParameterHolder()
        removePlatesParams.currentIssue = curIssue

        removePlatesParams
    }
}
