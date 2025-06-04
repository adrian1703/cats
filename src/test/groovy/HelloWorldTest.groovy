import sample.HelloWorld
import sample.HelloWorldKt
import spock.lang.Specification

class HelloWorldTest extends Specification {


    def "Hello World #name"() {
        given:
        def impl = toTest
        when:
        def result = impl.call()
        then:
        assert result == "Hello World"
        where:
        name   | toTest
        "java" | new HelloWorld()
        "kt"   | new HelloWorldKt()
    }
}
