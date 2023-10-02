package org.opendatamesh.odm.cli.commands;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
        name = "hello-world",
        description = "Says hello",
        subcommands = { HelpCommand.class }
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

