package uk.ac.sanger.scgcf.jira.lims.utils

import groovy.util.logging.Slf4j

/**
 * Created by as28 on 07/07/16.
 */

@Slf4j(value="LOG")
class MockFunctions {
    /**
     * Create a mock Jira ticket ID
     * @return string ticket id
     */
    static String createMockExternalId() {
        def extId = "UAT-${((int) (Math.random() * 1000000000)).toString()}"
        LOG.debug "Generated external ID = ${extId}"
        extId
    }
}
