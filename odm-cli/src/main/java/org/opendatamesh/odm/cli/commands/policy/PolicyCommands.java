package org.opendatamesh.odm.cli.commands.policy;

import org.opendatamesh.odm.cli.commands.policy.get.PolicyGetCommand;
import org.opendatamesh.odm.cli.commands.policy.list.PolicyListCommand;
import org.opendatamesh.odm.cli.commands.policy.publish.PolicyPublishCommand;
import org.opendatamesh.odm.cli.commands.policy.update.PolicyUpdateCommand;
import org.opendatamesh.odm.cli.commands.policy.validate.PolicyValidateCommand;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.InputManagerUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;

@Command(
        name = "policy",
        description = "commands to communicate with Policy microservice",
        version = "odm-cli policy 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                PolicyListCommand.class,
                PolicyGetCommand.class,
                PolicyPublishCommand.class,
                PolicyUpdateCommand.class,
                PolicyValidateCommand.class
        }
)
public class PolicyCommands implements Runnable{

    // Replace it with PolicyClient after refactoring RestUtils in policy service
    PolicyClientImpl policyClient;

    @Option(
            names = { "-s", "--server" },
            description = "URL of the Policy server. It must include the port. It overrides the value inside the properties file, if it is present"
    )
    String serverUrlOption;

    @Option(
            names = { "-f", "--properties-file" },
            description = "Path to the properties file",
            defaultValue = "./properties.yml"
    )
    String propertiesFileOption;

    // Replace it with PolicyClient after refactoring RestUtils in policy service
    public PolicyClientImpl getPolicyClient() {
        if (policyClient == null) {
            policyClient = setUpPolicyClient();
        }
        return policyClient;
    }

    // Replace it with PolicyClient after refactoring RestUtils in policy service
    private PolicyClientImpl setUpPolicyClient(){
        Properties properties = null;
        try {
            properties = FileReaderUtils.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = InputManagerUtils.getPropertyValue(properties, "policy-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The policy server URL wasn't specified. Use the -s option or create a file with the \"policy-server\" property");
            throw new RuntimeException("The policy server URL wasn't specified");
        }

        return new PolicyClientImpl(serverUrl, ObjectMapperUtils.getObjectMapper());
    }

    public static void main(String[] args) {
        CommandLine.run(new PolicyCommands(), args);
    }

    @Override
    public void run() {}

}
