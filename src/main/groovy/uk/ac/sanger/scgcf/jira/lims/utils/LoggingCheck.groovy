package uk.ac.sanger.scgcf.jira.lims.utils

import groovy.util.logging.Slf4j

/**
 * Created by as28 on 13/07/16.
 */

// To get debug messages you can use the object log (org.apache.log4j.Category).
// It's already binded to your script. So if you write
// log.error "Debug message"
// you will see this message in the atlassian-jira.log.
// You could implement different levels of debug messages.
// log.debug(), log.info(), log.warn() and log.error() and set the log level at the beginning of your script.
// log.setLevel(org.apache.log4j.Level.DEBUG)

// N.B. by defining class with the slf4j annotation you do not need to set up a static logging instance
// via LoggerFactory.getLogger(MyClass.class), you just use log
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
