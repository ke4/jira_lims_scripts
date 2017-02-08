package uk.ac.sanger.scgcf.jira.lims.utils

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.link.IssueLinkType
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.util.ErrorCollection
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.user.ApplicationUser
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader

/**
 * Utility class for getting Jira workflow related properties.
 *
 * Created by ke4 on 11/10/2016.
 */

@Slf4j(value = "LOG")
class WorkflowUtils {

    /**
     * Link a list of plates to the specified grouping issue and transition them if appropriate.
     *
     * @param plateActionParams a <code>PlateActionParameterHolder</code> object holding all the parameters
     * needed for adding a plate to the given grouping issue
     */
    public static void addPlatesToGivenGrouping(PlateActionParameterHolder plateActionParams) {
        executePlateAction(
            plateActionParams,
            { Issue mutableIssue ->
                createIssueLink(plateActionParams.currentIssue, mutableIssue, plateActionParams.linkTypeName)
            },
            { String plateIdString ->
                "Attempting to link plate with ID ${plateIdString} to ${plateActionParams.currentWorkflowName} workflow with ID ${plateActionParams.currentIssue.id}".toString()
            }
        )
    }

    /**
     * Remove the links between a list of plates and the specified grouping issue and transition them if appropriate.
     *
     * @param plateActionParams a <code>PlateActionParameterHolder</code> object holding all the parameters
     * needed for removing a plate from the given grouping issue
     */
    public static void removePlatesFromGivenGrouping(PlateActionParameterHolder plateActionParams) {
        executePlateAction(
                plateActionParams,
                { Issue mutableIssue ->
                    removeIssueLink(plateActionParams.currentIssue, mutableIssue, plateActionParams.linkTypeName)
                },
                { String plateIdString ->
                    "Removing link to plate with ID ${plateIdString} from ${plateActionParams.currentWorkflowName}".toString()
                }
        )
    }

    private static void executePlateAction(PlateActionParameterHolder plateActionParams, Closure actionToExecute, Closure messageClosure) {
        int actionId = ConfigReader.getTransitionActionId(plateActionParams.plateWorkflowName, plateActionParams.transitionName)

        plateActionParams.plateIds.each { String plateIdString ->
            Long plateIdLong = Long.parseLong(plateIdString)
            LOG.debug((String)messageClosure(plateIdString))

            MutableIssue mutableIssue = getMutableIssueForIssueId(plateIdLong)

            if(mutableIssue != null && mutableIssue.getIssueType().getName() == plateActionParams.issueTypeName) {

                actionToExecute(mutableIssue)

                if(mutableIssue.getStatus().getName() == plateActionParams.previousPlateState) {
                    transitionIssue(mutableIssue, actionId)
                }
            }
        }
    }

    /**
     * Gets the transition name by the given {@Issue} and actionID of the bounded transition variables.
     *
     * @param issue the current issue
     * @param transitionVars the current bounded transition variables
     * @return the name of the transition by the given {@Issue} and actionID of the bounded transition variables.
     */
    public static String getTransitionName(Issue issue, Map<String, Object> transitionVars) {
        JiraWorkflow workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue)
        def wfd = workflow.getDescriptor()
        def actionid = transitionVars["actionId"] as Integer

        wfd.getAction(actionid).getName()
    }

    /**
     * Get the current logged in application user
     * @return
     */
    public static ApplicationUser getLoggedInUser() {
        JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext()
        ApplicationUser user = authContext.getLoggedInUser()
        user
    }

    /**
     * Transitions an issue through the specified transition action
     *
     * @param issue the issue to be transitioned
     * @param actionId the transition action id
     * @return the error collection if the validation or transition fail, otherwise nothing
     */
    public static void transitionIssue(MutableIssue issue, int actionId) {
        // set up the transition
        WorkflowTransitionUtil wfTransUtil = JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)
        wfTransUtil.setIssue(issue)
        ApplicationUser user = getLoggedInUser()
        wfTransUtil.setUserkey(user.getKey())
        wfTransUtil.setAction(actionId)

        // validate the transition
        ErrorCollection ecValidate = wfTransUtil.validate()
        if(ecValidate.hasAnyErrors()) {
            LOG.error("Validation error transitioning plate issue with ID ${issue.getId()}".toString())
            // Get all non field-specific error messages
            Collection<String> stringErrors = ecValidate.getErrorMessages()
            stringErrors.eachWithIndex { String err, int i ->
                LOG.error("Error ${i}: ${err}".toString())
            }
            return
        }

        // perform the transition
        ErrorCollection ecProgress = wfTransUtil.progress()
        if(ecProgress.hasAnyErrors()) {
            LOG.error("Progress error transitioning plate issue with ID ${issue.getId()}".toString())
            // Get all non field-specific error messages
            Collection<String> stringErrors = ecProgress.getErrorMessages()
            stringErrors.eachWithIndex { String err, int i ->
                LOG.error("Error ${i}: ${err}".toString())
            }
        }
    }

    /**
     * Create a reciprocal issue link of the specified link type between two specified issues
     *
     * @param sourceIssue the issue that is the source of the link
     * @param destinationIssue the issue that is the destination of the link
     * @param linkTypeName the name of the issue link type
     */
    public static void createIssueLink(Issue sourceIssue, Issue destinationIssue, String linkTypeName) {
        // determine the issue link type
        ComponentManager cManager = ComponentManager.getInstance()
        IssueLinkTypeManager issLnkTMngr = cManager.getComponentInstanceOfType(IssueLinkTypeManager.class)
        IssueLinkType issLnkType = (issLnkTMngr.getIssueLinkTypesByName(linkTypeName))[0]

        // determine the user
        ApplicationUser user = getLoggedInUser()

        // link the issues together (will create a reciprocal link)
        IssueLinkManager issLnkMngr = ComponentAccessor.getIssueLinkManager()
        // throws a CreateException if it fails
        issLnkMngr.createIssueLink(sourceIssue.id, destinationIssue.id, issLnkType.id, 1L, user)
    }

    /**
     * Remove a reciprocal issue link of the specified type between two specified issues
     *
     * @param sourceIssue the issue that is the source of the link
     * @param destinationIssue the issue that is the destination of the link
     * @param linkTypeName the name of the issue link type
     */
    public static void removeIssueLink(Issue sourceIssue, Issue destinationIssue, String linkTypeName) {
        // determine the issue link type
        ComponentManager cManager = ComponentManager.getInstance()
        IssueLinkTypeManager issLnkTMngr = cManager.getComponentInstanceOfType(IssueLinkTypeManager.class)
        IssueLinkType issLnkType  = (issLnkTMngr.getIssueLinkTypesByName(linkTypeName))[0]

        // determine the user
        ApplicationUser user = getLoggedInUser()

        // remove the link between the plate issue and the submission issue
        IssueLinkManager issLnkMngr  = ComponentAccessor.getIssueLinkManager()
        IssueLink issueLink = issLnkMngr.getIssueLink(sourceIssue.id, destinationIssue.id, issLnkType.id)
        // throws IllegalArgumentException if the specified issueLink is null
        issLnkMngr.removeIssueLink(issueLink, user)
    }

    /**
     * Get the issue link type for a named issue link
     *
     * @param linkName
     * @return
     */
    public static IssueLinkType getIssueLinkType(String linkName) {

        // get the issue link type
        ComponentManager componentManager = ComponentManager.getInstance()
        IssueLinkTypeManager issLnkTMngr = componentManager.getComponentInstanceOfType(IssueLinkTypeManager.class)
        IssueLinkType issueLinkType  = (issLnkTMngr.getIssueLinkTypesByName(linkName))[0]

        issueLinkType
    }

    /**
     * Get the list of inward issue links for an issue id
     *
     * @param issueId
     * @return list of IssueLinks
     */
    public static List<IssueLink> getInwardLinksListForIssueId(Long issueId) {
        IssueLinkManager issLnkMngr  = ComponentAccessor.getIssueLinkManager()
        List<IssueLink> inwardLinksList = issLnkMngr.getInwardLinks(issueId)

        inwardLinksList
    }

    /**
     * Get the list of outward links for an issue id
     *
     * @param issueId
     * @return list of IssueLinks
     */
    public static List<IssueLink> getOutwardLinksListForIssueId(Long issueId) {
        IssueLinkManager issLnkMngr = ComponentAccessor.getIssueLinkManager()
        List<IssueLink> outwardLinksList = issLnkMngr.getOutwardLinks(issueId)

        outwardLinksList
    }

    /**
     * Get a mutable issue for an issue id
     *
     * @param issueId
     * @return mutable version of the issue
     */
    public static MutableIssue getMutableIssueForIssueId(Long issueId) {
        IssueManager issMngr = ComponentAccessor.getIssueManager()
        MutableIssue mutableIssue = issMngr.getIssueObject(issueId)

        mutableIssue
    }
}
