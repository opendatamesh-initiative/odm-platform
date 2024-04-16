package org.opendatamesh.odm.cli.commands.policy.publish;

import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "publish",
        description = "Commands to publish objects related to Policy microservice",
        version = "odm-cli policy publish 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                PublishPolicyEngineCommand.class,
                PublishPolicyCommand.class,
                PublishPolicyEvaluationResultCommand.class
        }
)
public class PolicyPublishCommand implements Runnable {

    @ParentCommand
    protected PolicyCommands policyCommands;

    @Override
    public void run() { }

}
