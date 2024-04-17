package org.opendatamesh.odm.cli.commands.policy.get;

import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;


@Command(
        name = "get",
        description = "Commands to get objects related to Policy microservice",
        mixinStandardHelpOptions = true,
        version = "odm-cli policy get 1.0.0",
        subcommands = {
                GetPolicyEngineCommand.class,
                GetPolicyCommand.class,
                GetPolicyEvaluationResultCommand.class
        }
)
public class PolicyGetCommand implements Runnable {

    @ParentCommand
    protected PolicyCommands policyCommands;

    @Override
    public void run() { }

}
