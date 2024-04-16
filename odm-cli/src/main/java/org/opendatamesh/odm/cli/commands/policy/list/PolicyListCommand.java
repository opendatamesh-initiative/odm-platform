package org.opendatamesh.odm.cli.commands.policy.list;

import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Commands to list objects related to Policy microservice",
        mixinStandardHelpOptions = true,
        version = "odm-cli policy list 1.0.0",
        subcommands = {
                ListPolicyEngineCommand.class,
                ListPolicyCommand.class,
                ListPolicyEvaluationResultCommand.class
        }
)
public class PolicyListCommand implements Runnable {

    @ParentCommand
    protected PolicyCommands policyCommands;

    @Override
    public void run() {}

}
