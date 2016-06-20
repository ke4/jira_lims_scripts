package actions

import org.apache.log4j.*
import groovy.util.logging.*

@Log4j
class HelloWorld{
    static def execute() {
        println "Hello world starting"

        // TODO: Need to set the logs level from region-specific config
        log.level = Level.INFO

        // add an appender to logs to file
        // TODO: where to logs to, the Atlassian logs? path needs to come from region-specific config
        log.addAppender(new FileAppender(new TTCCLayout(), '../../../../logs/test.logs'));

        // this will NOT print/write as the loglevel is INFO
        log.debug 'Execute actions.HelloWorld.'

        // these will all print as equal or more severe than loglevel.INFO
        log.info 'Simple sample to show logs field is injected.'
        log.warn 'Simple sample to show logs WARN field is injected.'
        log.error 'Simple sample to show logs ERR field is injected.'

        println "Hello world completed"

        return "My first script!"
    }
}

def helloWorld = new HelloWorld()
return helloWorld.execute()