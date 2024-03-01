package org.opendatamesh.platform.pp.policy.server;

import org.opendatamesh.platform.core.commons.clients.ODMIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;

//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMPolicyApp.class })
public class ODMPolicyIT extends ODMIntegrationTest {

}
