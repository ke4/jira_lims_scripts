package uk.ac.sanger.scgcf.jira.lims.utils

import groovy.util.logging.Slf4j

/**
 * The {@code LoggingCheck} class is used to test logging is working.
 *
 * For scripts running in JIRA you can use the bound log (org.apache.log4j.Category) and levels e.g.
 * log.debug("msg"), log.info("msg"), log.warn("msg") and log.error("msg")
 * You can set the log level at the beginning of your script e.g.
 * log.setLevel(org.apache.log4j.Level.DEBUG)
 *
 * For classes you can define them with the slf4j annotation e.g.
 * @Slf4j(value="LOG")
 *
 * In JIRA you can set the logging level visibility in the system settings under Administration ->
 * logging and profiling -> Configure logging level for another package and add one for uk.ac.sanger.scgcf.jira
 * N.B. to make this persist between sessions you need to edit WEB-INF/classes/log4j.properties in the JIRA home
 * directory.
 *
 * Created by as28 on 13/07/16.
 */

@Slf4j(value="LOG")
class LoggingCheck {
    def execute() {
        LOG.debug "Logging check starting"

        // TODO: how set which logging level is used for unit testing in intellij?
        // TODO: how set which logging level is used for unit testing in jira?
        // TODO: ideally the debug level should come from a config file outside the project code
        LOG.trace 'TRACE is shown'
        LOG.debug 'DEBUG is shown'
        LOG.info 'INFO is shown'
        LOG.warn 'WARN is shown'
        LOG.error 'ERROR is shown'

        // to interject variables it is faster to do it this way rather than interpolate the variables in the string
        // as this way it only does the interpolation when actually logging at this level
        // log.debug("There are now {} user accounts: {}", count, userAccountList)

        LOG.debug "Logging check completed"

        "Finished Logging Check"
    }

}
