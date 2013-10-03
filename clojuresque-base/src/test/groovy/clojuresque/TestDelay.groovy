package clojuresque

import spock.lang.Specification

class TestDelay extends Specification {
    def "a delay returns the delayed value"() {
        given: "a value"
        def x = "x"

        when: "delaying it"
        def d = new Delay({-> x + "y"})

        then: "the delay will return it"
        d.value == "xy"
    }

    def "delaying a none Closure is just the identity"() {
        given: "a value"
        def x = "x"

        when: "delaying the value without a Closure"
        def d = new Delay(x)

        and: "changing the value"
        x = "y"

        then: "then the old value is kept"
        d.value == "x"
    }

    def "forcing a delay will get the delayed value"() {
        given: "a value"
        def x = "x"

        when: "delaying it"
        def d = new Delay({-> x})

        and: "changing the original value"
        x = "y"

        then: "the change will be picked up"
        Delay.force(d) == "y"

        when: "changing the original value again"
        x = "z"

        then: "the delay will keep the force value"
        Delay.force(d) == "y"
    }

    def "forcing any other object is the identity"() {
        given: "a value"
        def x = "x"

        expect: "forcing it to be the identity"
        Delay.force(x) == x
    }
}
