package uk.ac.sanger.scgcf.jira.lims.actions
//
//import com.atlassian.jira.component.ComponentAccessor
//import com.atlassian.jira.event.type.EventDispatchOption
//import com.atlassian.jira.issue.fields.CustomField
//import com.atlassian.jira.security.JiraAuthenticationContext
//import com.atlassian.jira.user.ApplicationUser
//import org.junit.Test
import spock.lang.Specification

class UATFunctionsTest extends Specification {

    def "basic test"() {

        expect:
        assert true == true
    }

//    def 'test create customer tubes'() {
//
//        setup: "Create a real issue with custom fields to test against"
//
//        ComponentAccessor componentAccessor = new ComponentAccessor()
//        JiraAuthenticationContext authenticationContext = componentAccessor.getJiraAuthenticationContext()
//        ApplicationUser currentUser = authenticationContext.getLoggedInUser()
//
//        def issueFactory = componentAccessor.getIssueFactory()
//        def issueManager = componentAccessor.getIssueManager()
//
//        // create an issue locally (not saved yet)
//        def tmpIssue=issueFactory.getIssue()
//
////        TODO: get issue type ID from config
//        tmpIssue.setIssueTypeId("10100") // Type task
//        tmpIssue.setSummary("test summary text")
////        TODO: make tmp project at beginning as part of all tests
//        tmpIssue.setProjectId(10500L) // this is the UAT testing project
//        tmpIssue.setReporter(currentUser)
//
//        // set the custom field value for the issue barcodes fields
//        // TODO: field object name must come from config
//        CustomField customFieldCustTubeBCs=componentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10900")
//        tmpIssue.setCustomFieldValue(customFieldCustTubeBCs,"test value text")
//
//        // save the issue to the database
//        def testIssue = issueManager.createIssueObject(currentUser,(Map)[issue:tmpIssue])
//
//        when: "Call the function to create the tubes and materials"
//        UATFunctions.createCustomerTubes(testIssue)
//
//        then: "Check the issue custom fields to have been updated"
//        assert tmpIssue != null
//        assert tmpIssue.getIssueTypeId() == "10100"
//        assert tmpIssue.getSummary() == "test summary text"
//        assert tmpIssue.getCustomFieldValue(customFieldCustTubeBCs) == "test value text"
//
//        assert testIssue != null
//        assert testIssue.getSummary() == "test summary text"
//        assert testIssue.getCustomFieldValue(customFieldCustTubeBCs) == "test value text"
//
//        cleanup:
//        // delete the issue from the database
//        if ( testIssue != null ) {
//            issueManager.deleteIssue(currentUser, testIssue, EventDispatchOption.ISSUE_DELETED, false);
//        }
//
//        // TODO: teardown temp project at end of all tests
//    }




//    def 'test can create customer tubes'() {
//        setup:
//
////        SearchService searchService = mock(SearchService.class)
////        UserUtil userUtil = mock(UserUtil.class)
//
//        // create custom fields
//        MockCustomFieldType mockCustomFieldType = new MockCustomFieldType("String", "String")
////        TextAreaCFType mockCustomFieldType = new MockTextAreaCFType()
//        MockCustomField mockCustomFieldCustTubeBCs = new MockCustomField("1001","UAT cust tube barcodes", mockCustomFieldType)
//        MockCustomField mockCustomFieldCustTubeDetails = new MockCustomField("1002","UAT cust tube details", mockCustomFieldType)
//
//
//
//
//
////        ComponentAccessor mockComponentAccessor = mock(ComponentAccessor.class)
//
//        // create issue
//        MockIssue mockIssue = new MockIssue(2001, "Test-01")
//        mockIssue.setSummary("test summary")
//
//        // create field manager
//        MockCustomFieldManager mockCustomFieldManager = new MockCustomFieldManager()
//        mockCustomFieldManager.addCustomField(mockCustomFieldCustTubeBCs)
//        mockCustomFieldManager.addCustomField(mockCustomFieldCustTubeDetails)
//
//
//        // Create a list of project contexts for which the custom field needs to be available
//        List<GenericValue> issueTypes = new ArrayList<GenericValue>()
//        issueTypes.add(null)
//
//        List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
//        contexts.add(GlobalIssueContext.getInstance());
//
//        CustomFieldType fieldType = mockCustomFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:textarea")
//
//        println fieldType.getClass()
//
//        CustomFieldSearcher fieldSearcher = mockCustomFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:textsearcher");
//
//        // Add custom field
//        CustomField cField = mockCustomFieldManager.createCustomField("1003", "BAR", fieldType, fieldSearcher, contexts, issueTypes);
//
//        MockCustomField cField2 = new MockCustomField("1004","Test", fieldType)
//        mockCustomFieldManager.addCustomField(cField2)
//
//        println cField2.getCustomFieldType()
//
////        mockCustomFieldManager.getCustomFieldType(CustomFieldConstants.)
//
////        println mockCustomFieldManager.dump()
//
//        //TODO: custom fields are NOT being added to manager using addCustomField
//
//        mockIssue.setCustomFieldValue(mockCustomFieldCustTubeBCs, "1")
//        mockIssue.setCustomFieldValue(mockCustomFieldCustTubeDetails, "2")
//        mockIssue.setCustomFieldValue(cField, "3")
//        mockIssue.setCustomFieldValue(cField2, "4")
////
////        List<CustomField> cfmList = mockCustomFieldManager.getCustomFieldObjects(mockIssue)
////        cfmList.eachWithIndex{ CustomField cf, int indx ->
////            log.debug "custom field name = ${cf.name}"
////        }
////
////        // group the mocks under a MockComponentWorker
////        new MockComponentWorker()
////            .addMock(CustomFieldManager.class, mockCustomFieldManager)
////            .addMock(Issue.class, mockIssue)
////            .init()
//
//        MockProject mockProject = new MockProject()
//        mockIssue.setProjectObject(mockProject)
//
////        when:
////        UATFunctions.createCustomerTubes(mockIssue)
//
//        expect:
//        assert mockCustomFieldCustTubeBCs.getId() == "1001"
//        assert mockCustomFieldCustTubeBCs.getName() == "UAT cust tube barcodes"
//        assert mockCustomFieldCustTubeBCs.getCustomFieldType().getKey() == "String"
//        assert mockCustomFieldCustTubeBCs.getCustomFieldType().getName() == "String"
//
//        assert mockCustomFieldCustTubeDetails.getId() == "1002"
//        assert mockCustomFieldCustTubeDetails.getName() == "UAT cust tube details"
//        assert mockCustomFieldCustTubeDetails.getCustomFieldType().getKey() == "String"
//        assert mockCustomFieldCustTubeDetails.getCustomFieldType().getName() == "String"
////
//        assert mockIssue.getId() == 2001L
//        assert mockIssue.getKey() == "Test-01"
//        assert mockIssue.getSummary() == "test summary"
//
//        assert mockCustomFieldManager != null
//        assert mockCustomFieldManager.exists("customfield_1001")
//        assert mockCustomFieldManager.exists("customfield_1002")
////        assert mockCustomFieldManager.exists("customfield_1003")
//        assert mockCustomFieldManager.exists("customfield_1004")
////
////
////        assert cfmList != null
//
////        assert mockIssue.getCustomFieldValue(mockCustomFieldCustTubeBCs) == "1"
////        assert mockIssue.getCustomFieldValue(mockCustomFieldCustTubeDetails) == "2"
////        assert mockIssue.getCustomFieldValue(cField) == "3"
//        assert mockIssue.getCustomFieldValue(cField2) == "4"
//    }
}
