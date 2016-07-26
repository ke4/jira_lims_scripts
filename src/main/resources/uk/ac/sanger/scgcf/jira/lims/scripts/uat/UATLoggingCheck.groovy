package uk.ac.sanger.scgcf.jira.lims.scripts.uat

import uk.ac.sanger.scgcf.jira.lims.utils.LoggingCheck
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

LOG.error "Starting logging checks with error"
LOG.debug "Starting Logging check call"
def lc = new LoggingCheck()
lc.execute()
LOG.debug "Back from Logging check call, testing method call"
myMethod()
LOG.debug "Finished Logging checks"

def myMethod() {
    LOG.debug "Entered myMethod"
}


