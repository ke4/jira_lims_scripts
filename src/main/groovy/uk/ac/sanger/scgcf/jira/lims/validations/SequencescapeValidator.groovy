package uk.ac.sanger.scgcf.jira.lims.validations

import groovyx.net.http.Method
import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader
import uk.ac.sanger.scgcf.jira.lims.exceptions.RestServiceException
import uk.ac.sanger.scgcf.jira.lims.utils.RestService

import static groovyx.net.http.ContentType.JSON

/**
 * The {@code SequencescapeValidator} class holds various Sequencescape related validations.
 * It can validate a project existence by its name and a study existence by its name.
 * It is using Sequencescape's Internal REST Service API for doing the validation.
 *
 * Created by ke4 on 03/10/2016.
 */
class SequencescapeValidator {

    public static String SS_PROJECT_NOT_EXISTS_ERROR_MESSAGE = "The entered Sequencescape Project Name does not exist"
    public static String SS_STUDY_NOT_EXISTS_ERROR_MESSAGE = "The entered Sequencescape Study Name does not exist"

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
        validateProjectOrStudyName(projectName, ConfigReader.getSequencescapeDetails()['searchProjectByName'] as String)
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
        validateProjectOrStudyName(studyName, ConfigReader.getSequencescapeDetails()['searchStudyByName'] as String)
    }

    private boolean validateProjectOrStudyName(String name, String search) {
        def requestBody = [
                "search": [
                        "name": name
                ]
        ]
        def responseMap = restService.request(Method.POST, JSON, search, requestBody)
        def response = responseMap['response']
        def reader = responseMap['reader']

        if (response.status == 301) {
            true
        } else if (response.status == 404) {
            false
        } else {
            throw new RestServiceException("The request was not successful. The server responded with ${response.status} code."
                    + System.getProperty("line.separator")
                    + " The error message is: $reader"
                    + System.getProperty("line.separator")
                    + "URL: ${restService.httpBuilder.uri}/$servicePath"
                    + System.getProperty("line.separator")
                    + "Request: $requestBody");
        }
    }
}
