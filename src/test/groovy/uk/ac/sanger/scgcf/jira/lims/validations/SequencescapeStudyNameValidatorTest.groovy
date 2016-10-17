package uk.ac.sanger.scgcf.jira.lims.validations

import groovyx.net.http.Method
import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import static groovyx.net.http.ContentType.JSON

/**
 * Created by ke4 on 05/10/16.
 */
class SequencescapeStudyNameValidatorTest extends Specification {

    def "find invalid study name in Sequencescape should return false"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String invalidStudyName = "invalid"
        def params = [
                "search": [
                        "name": invalidStudyName
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
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchStudyByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        expect: "Check if validation failed"
        assert !validator.validateStudyName(invalidStudyName)
    }

    def "find valid study name in Sequencescape should return true"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String validStudyName = "100 cycle test"
        def params = [
                "search": [
                        "name": validStudyName
                ]
        ]

        def responseStatus = 301
        def responseJSON = [
            "study": [
                "uuid": "12345678-1111-2222-4444-123456789012",
                "abbreviation": "123STDY",
                "abstract": null,
                "accession_number": null,
                "commercially_available": "No",
                "contains_human_dna": "Yes",
                "contaminated_human_dna": "No",
                "data_release_sort_of_study": "genomic sequencing",
                "data_release_strategy": "not applicable",
                "description": "testing",
                "ethically_approved": true,
                "name": "test",
                "reference_genome": " ",
                "remove_x_and_autosomes": false,
                "sac_sponsor": "Me",
                "separate_y_chromosome_data": false,
                "state": "active",
                "type": "Test Sequencing"
            ]
        ]
        def responseMap = [
                response: [status: responseStatus],
                reader: responseJSON
        ]

        restServiceStub.request(
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchStudyByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        expect: "Check if validation succeed"
        assert validator.validateStudyName(validStudyName)
    }

    def "when the remote service errors should throw exception"() {

        setup: "Create a mock RestService, its parameters and the mocked response"
        def restServiceStub = Stub(RestService)
        String someStudyName = "some study"
        def params = [
                "search": [
                        "name": someStudyName
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
                Method.POST, JSON, ConfigReader.getSequencescapeDetails()['searchStudyByName'] as String, params) >> responseMap

        def validator = new SequencescapeValidator()
        validator.restService = restServiceStub

        when: "unexpected condition was encountered"
        validator.validateStudyName(someStudyName)

        then:
        RestServiceException ex = thrown()
        ex.message ==
            "The request was not successful. The server responded with 501 code. The error message is: $responseJSON".toString()
    }
}
