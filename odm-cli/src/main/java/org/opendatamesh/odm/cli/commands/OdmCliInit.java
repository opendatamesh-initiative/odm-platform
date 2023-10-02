package org.opendatamesh.odm.cli.commands;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(
        name = "odm-cli",
        description = "ODM CLI init method",
        subcommands = { HelpCommand.class }
)
public class OdmCliInit implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new OdmCliInit(), args);
    }

    @Override
    public void run() {
        System.out.println("Welcome to ODM CLI");
    }

}
