package com.jdwsearch.infrastructure

import spock.lang.Specification

class LoggerConfigurationSpecTest extends Specification {

    def 'should not throw after configuring logger'() {
        when:
            LoggerConfiguration.configureLogger()

        then:
            noExceptionThrown()
    }

}
