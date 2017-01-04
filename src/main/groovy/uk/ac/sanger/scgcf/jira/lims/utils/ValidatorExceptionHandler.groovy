package uk.ac.sanger.scgcf.jira.lims.utils

import com.opensymphony.workflow.InvalidInputException
import groovy.util.logging.Slf4j

/**
 * Common exception handler class to catch and wrap the thrown exception,
 * log it (optionally) and rethrow it wrapped to an <code>InvalidInputException</code>.
 * JIRA will catch the <code>InvalidInputException</code> and show the message on the screen
 * in case a form handling.
 * This is a workaround. IMHO JIRA or ScriptRunner should handle this situations and not our script.
 * It could be replaced in the future, if we would found a better solution.
 *
 * Created by ke4 on 13/12/2016.
 */
@Slf4j(value = "LOG")
class ValidatorExceptionHandler {

    /**
     * Log and rethrow the given exception.
     *
     * @param cause the exception to log and rethrow
     * @param errorMessage the primary error message to log
     * @param additionalMessage the additional error message to log
     */
    public static void throwAndLog(Throwable cause, String errorMessage, String additionalMessage) {
        LOG.error(errorMessage)
        LOG.error(additionalMessage)

        throwException(cause)
    }

    /**
     * Rethrow the given exception. If it is not an <code>InvalidInputException</code>,
     * then it is wrap the original one to an <code>InvalidInputException</code>.
     *
     * @param cause the original exception to rethrow
     * @throws InvalidInputException
     */
    public static void throwException(Throwable cause) throws InvalidInputException {
        if (cause instanceof InvalidInputException) {
            throw cause
        } else {
            throw new InvalidInputException(cause)
        }
    }
}
