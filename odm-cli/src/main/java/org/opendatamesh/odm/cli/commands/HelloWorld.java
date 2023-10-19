package org.opendatamesh.odm.cli.commands;


import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "hello-world",
        description = "Says hello",
        version = "odm-cli hello-world 1.0.0",
        mixinStandardHelpOptions = true
)
public class HelloWorld implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new HelloWorld(), args);
    }

    @Override
    public void run() {
        System.out.println("Hello World");
    }

}

