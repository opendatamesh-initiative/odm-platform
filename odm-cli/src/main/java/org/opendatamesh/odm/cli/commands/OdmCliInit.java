package org.opendatamesh.odm.cli.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "odm-cli",
        description = "ODM CLI init method",
        version = "odm-cli 1.0.0",
        mixinStandardHelpOptions = true
)
public class OdmCliInit implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new OdmCliInit(), args);
    }

    @Override
    public void run() { }

}
