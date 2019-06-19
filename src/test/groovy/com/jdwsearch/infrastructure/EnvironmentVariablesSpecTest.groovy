package com.jdwsearch.infrastructure

import spock.lang.Specification
import spock.lang.Unroll

class EnvironmentVariablesSpecTest extends Specification {

    @Unroll
    def 'should return #expectedOutput for name #givenName with environment #environment'() {
        when:
            def actualOutput = new EnvironmentVariables(environment as Map<String, String>).get givenName

        then:
            actualOutput == expectedOutput

        where:
            environment                   | givenName | expectedOutput
            []                            | 'any'     | Optional.empty()
            [test: '123']                 | 'test'    | Optional.of('123')
            [first: '123', second: '456'] | 'second'  | Optional.of('456')
    }

    def 'should return transformed variable'() {
        when:
            def result = new EnvironmentVariables([test: '123']).get('test', Integer.&parseInt).get()

        then:
            result == 123
            result instanceof Integer
    }

    def 'should return empty optional if transformation function throws'() {
        when:
            def result = new EnvironmentVariables([test: 'not a number']).get('test', Integer.&parseInt)

        then:
            noExceptionThrown()
            !result.isPresent()
    }

}
