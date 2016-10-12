package uk.ac.sanger.scgcf.jira.lims.validations

import com.atlassian.jira.action.issue.customfields.MockCustomFieldType
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.MockCustomField
import com.atlassian.jira.mock.component.MockComponentWorker
import com.opensymphony.workflow.InvalidInputException
import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.service_wrappers.JiraAPIWrapper

/**
 * Created by ke4 on 05/10/16.
 */
class MandatoryFieldsValidatorTest extends Specification {

    def "not filled mandatory fields should return an InvalidInputException"() {

        setup: "Two mocked custom fields with empty value and one with some value and instantiate the mandatory field validator"
        new MockComponentWorker().init();

        def mandatoryFieldNames = [ "fieldName1", "fieldName2", "fieldName3"]
        def mockedCustomFieldIDs = [ "mockedCF1", "mockedCF2", "mockedCF3"]
        def mockedCustomFieldNames = [ "mockedCF1Name", "mockedCF2Name", "mockedCF3Name"]
        def issueStub = Stub(Issue)
        def mockCustomFieldType = new MockCustomFieldType()

        GroovyMock(JiraAPIWrapper, global:true)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[1]) >>
                new MockCustomField(mockedCustomFieldIDs[1], mockedCustomFieldNames[1], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[2]) >>
                new MockCustomField(mockedCustomFieldIDs[2], mockedCustomFieldNames[2], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[3]) >>
                new MockCustomField(mockedCustomFieldIDs[3], mockedCustomFieldNames[3], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[1]) >> ""
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[2]) >> ""
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[3]) >> "someValue"

        def mandatoryFieldValidator = new MandatoryFieldValidator()

        when: "some mandatory fields has not been filled"
        mandatoryFieldValidator.validate(issueStub, mandatoryFieldNames)

        then: "InvalidInputException should be thrown"
        InvalidInputException ex = thrown()
        Map<String, String> errorMessagesMap = ex.getErrors()
        ex.genericErrors[0] == MandatoryFieldValidator.COMMON_ERROR_MESSAGE
        errorMessagesMap.size() == 2
        errorMessagesMap.get(mockedCustomFieldIDs[1]) == "You must specify a value for ${mockedCustomFieldNames[1]}.".toString()
        errorMessagesMap.get(mockedCustomFieldIDs[2]) == "You must specify a value for ${mockedCustomFieldNames[2]}.".toString()
    }

    def "when all mandatory fields filled then the mandatory field validator should pass"() {

        setup: "Three mocked custom fields with some values and instantiate the mandatory field validator"
        new MockComponentWorker().init();

        def mandatoryFieldNames = [ "fieldName1", "fieldName2", "fieldName3"]
        def mockedCustomFieldIDs = [ "mockedCF1", "mockedCF2", "mockedCF3"]
        def mockedCustomFieldNames = [ "mockedCF1Name", "mockedCF2Name", "mockedCF3Name"]
        def issueStub = Stub(Issue)
        def mockCustomFieldType = new MockCustomFieldType()

        GroovyMock(JiraAPIWrapper, global:true)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[1]) >>
                new MockCustomField(mockedCustomFieldIDs[1], mockedCustomFieldNames[1], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[2]) >>
                new MockCustomField(mockedCustomFieldIDs[2], mockedCustomFieldNames[2], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldByName(mandatoryFieldNames[3]) >>
                new MockCustomField(mockedCustomFieldIDs[3], mockedCustomFieldNames[3], mockCustomFieldType)
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[1]) >> "some value1"
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[2]) >> "some value2"
        JiraAPIWrapper.getCustomFieldValueByName(issueStub, mandatoryFieldNames[3]) >> "some value3"

        def mandatoryFieldValidator = new MandatoryFieldValidator()

        expect: "validator returns true"
        assert mandatoryFieldValidator.validate(issueStub, mandatoryFieldNames)
    }

}
