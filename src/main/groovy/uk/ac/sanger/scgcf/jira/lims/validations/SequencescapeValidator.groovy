package uk.ac.sanger.scgcf.jira.lims.validations

import groovyx.net.http.Method
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.RestService
import uk.ac.sanger.scgcf.jira.lims.utils.SequencescapeConstants

import static groovyx.net.http.ContentType.JSON

/**
 * The {@code SequencescapeValidator} class holds various Sequencescape related validations.
 * It can validate a project existence by its name and a study existence by its name.
 * It is using Sequencescape's Internal REST Service API for doing the validation.
 *
 * Created by ke4 on 03/10/2016.
 */
class SequencescapeValidator {

    RestService restService = new RestService()

    /**
     * Validates if the given project exists in Sequencescape.
     * If it exists, then returns true; otherwise false.
     * If the response contains some unexpected response code, then it throws {@code RestServiceException}.
     *
     * @param projectName the name of the project to query in Sequencescape
     * @return true if the project exists, otherwise false
     * @throws RestServiceException
     */
    public boolean validateProjectName(String projectName) throws RestServiceException {
        def params = ["projectname": projectName]
        validateProjectOrStudyName(params, SequencescapeConstants.SEARCH_PROJECT_BY_NAME_UUID)
    }

    /**
     * Validates if the given study exists in Sequencescape.
     * If it exists, then returns true; otherwise false.
     * If the response contains some unexpected response code, then it throws {@code RestServiceException}.
     *
     * @param studyName the name of the study to query in Sequencescape
     * @return true if the study exists, otherwise false
     * @throws RestServiceException
     */
    public boolean validateStudyName(String studyName) throws RestServiceException {
        def params = ["studyname": studyName]
        validateProjectOrStudyName(params, SequencescapeConstants.SEARCH_STUDY_BY_NAME_UUID)
    }

    private boolean validateProjectOrStudyName(Map<String, String> params, String search) {
        def responseMap = restService.request(Method.POST, JSON, search, params)
        def response = responseMap['response']
        def reader = responseMap['reader']

        if (response.status == 301) {
            true
        } else if (response.status == 404) {
            false
        } else {
            throw new RestServiceException("The request was not successful. The server responded with ${response.status} code."
                    + " The error message is: $reader"
            )
        }
    }
}
