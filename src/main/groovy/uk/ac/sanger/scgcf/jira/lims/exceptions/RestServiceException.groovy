package uk.ac.sanger.scgcf.jira.lims.exceptions

import com.opensymphony.workflow.InvalidInputException

/**
 * The {@code RestServiceException} class represents an exception wrapper
 * for the error messages coming from the Internal REST Service.
 *
 * Created by ke4 on 05/10/2016.
 */
class RestServiceException extends InvalidInputException {

    RestServiceException(message) {
        super(message)
    }
}
