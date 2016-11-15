package uk.ac.sanger.scgcf.jira.lims.utils

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
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

/**
 * Utility class for getting Jira workflow related properties.
 *
 * Created by ke4 on 11/10/2016.
 */
class WorkflowUtils {

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
    public static ErrorCollection transitionIssue(MutableIssue issue, int actionId) {
        // set up the transition
        WorkflowTransitionUtil wfTransUtil = JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)
        wfTransUtil.setIssue(issue)
        ApplicationUser user = getLoggedInUser()
        wfTransUtil.setUserkey(user.getKey())
        wfTransUtil.setAction(actionId)

        // validate the transition
        ErrorCollection ec1 = wfTransUtil.validate()
        if(ec1.hasAnyErrors()) {
            return ec1
        }

        // perform the transition
        ErrorCollection ec2 = wfTransUtil.progress()
        if(ec2.hasAnyErrors()) {
            return ec2
        }
        return null
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
}
