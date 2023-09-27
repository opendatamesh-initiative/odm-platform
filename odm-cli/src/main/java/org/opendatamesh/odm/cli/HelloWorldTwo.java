package org.opendatamesh.odm.cli;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(
        name = "hello-two",
        description = "Says hello",
        subcommands = { HelpCommand.class }
)
public class HelloWorldTwo implements Runnable {

    @Option(names = {"-n", "--name"}, description = "Your name")
    private String name;

    public static void main(String[] args) {
        CommandLine.run(new HelloWorldTwo(), args);
    }

    @Override
    public void run() {
        System.out.println("Hello World: " + name);
    }
}
