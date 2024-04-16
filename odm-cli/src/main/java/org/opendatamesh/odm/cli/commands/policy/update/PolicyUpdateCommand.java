package org.opendatamesh.odm.cli.commands.policy.update;

import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "update",
        description = "Commands to update objects related to Policy microservice",
        version = "odm-cli policy update 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                UpdatePolicyEngineCommand.class,
                UpdatePolicyCommand.class,
                UpdatePolicyEvaluationResultCommand.class
        }
)
public class PolicyUpdateCommand implements Runnable {

    @ParentCommand
    protected PolicyCommands policyCommands;

    @Override
    public void run() { }

}
