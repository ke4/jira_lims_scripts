package uk.ac.sanger.scgcf.jira.lims.scripts.uat

import uk.ac.sanger.scgcf.jira.lims.actions.UATFunctions
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// create logging class
@Field private final Logger LOG = LoggerFactory.getLogger(getClass())

LOG.debug "Testing creation of customer tubes"
def tubeBarcodes, tubeDetails
(tubeBarcodes, tubeDetails) = UATFunctions.createCustomerTubes()

"tubeBarcodes = ${tubeBarcodes}"