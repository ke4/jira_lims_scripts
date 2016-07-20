package uk.ac.sanger.scgcf.jira.lims.utils

import spock.lang.Specification

/**
 * Created by as28 on 13/07/16.
 */
class LoggingCheckTest extends Specification {
    def "test execute logging check"() {

        setup: "Create a logging check class instance"
        def lc = new LoggingCheck()

        expect: "Check expected string returned"
        assert lc.execute() == "Finished Logging Check"
    }
}
