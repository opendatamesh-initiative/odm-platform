package org.opendatamesh.odm.cli.commands.policy.validate;

import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "validate",
        description = "Commands to validate JSON documents with policies stored in the Policy microservice",
        version = "odm-cli policy validate 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = { ValidateDocumentCommand.class }
)
public class PolicyValidateCommand implements Runnable {

    @ParentCommand
    protected PolicyCommands policyCommands;

    @Override
    public void run() { }

}
