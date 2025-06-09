package demo

import java.util.concurrent.Callable

class HelloWorldKt : Callable<String> {

    override fun call(): String {
        return "Hello World"
    }
}