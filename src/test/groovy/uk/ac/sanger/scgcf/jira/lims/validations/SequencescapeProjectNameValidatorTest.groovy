package uk.ac.sanger.scgcf.jira.lims.validations

import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.Method
import spock.lang.Specification

/**
 * Created by ke4 on 05/10/16.
 */
class SequencescapeProjectNameValidatorTest extends Specification {

    def "find invalid project name in Sequencescape should return false"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String invalidProjectName = "invalid"
        def params = [
                         "search": [
                             "name": invalidProjectName
                         ]
                     ]

        def responseStatus = 404
        def responseJSON = [
                "general": [
                        "no resources found with that search criteria"
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchProjectByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        expect: "Check if validation failed"
        assert !validator.validateProjectName(invalidProjectName)
    }

    def "find valid project name in Sequencescape should return true"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String validProjectName = "100 cycle test"
        def params = [
                "search": [
                        "name": validProjectName
                ]
        ]

        def responseStatus = 301
        def responseJSON = [
                [
                    "project": [
                        "created_at": "2009-03-02 10:41:12 +0000",
                        "updated_at": "2014-04-07 16:15:42 +0100",
                        "uuid": "0ff51cfa-1234-5678-1234-00144f2062b9",
                        "approved": true,
                        "budget_cost_centre": null,
                        "budget_division": "R+D",
                        "collaborators": null,
                        "cost_code": "S1234",
                        "external_funding_source": null,
                        "funding_comments": "test run",
                        "funding_model": null,
                        "name": "test",
                        "project_manager": "John Smith",
                        "state": "active"
                    ]
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchProjectByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        expect: "Check if validation succeed"
        assert validator.validateProjectName(validProjectName)
    }

    def "when the remote service errors should throw exception"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String someProjectName = "some project"
        def params = [
                "search": [
                        "name": someProjectName
                ]
        ]

        def responseStatus = 501
        def responseJSON = [
                "general": [
                        "requested action is not supported on this resource"
                ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchProjectByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        when: "unexpected condition was encountered"
        validator.validateProjectName(someProjectName)

        then:
        RestServiceException ex = thrown()
        ex.message ==
            "The request was not successful. The server responded with 501 code. The error message is: $responseJSON".toString()
    }
}
