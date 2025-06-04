package demo;

import java.util.concurrent.Callable;

public class HelloWorld implements Callable<String> {

    @Override
    public String call(){
        return "Hello World";
    }
}
