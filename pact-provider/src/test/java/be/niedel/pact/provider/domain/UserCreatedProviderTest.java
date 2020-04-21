package be.niedel.pact.provider.domain;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider("UserProvider")
@PactBroker
@VerificationReports(value = {"json"})
@IgnoreNoPactsToVerify
public class UserCreatedProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedProviderTest.class);
    private static final String ID = "4de42227-d5a3-4a22-993f-dec0cfdaffed";
    protected static final String NICKNAME = "jimmy_drop_tables";

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new AmpqTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("SomeProviderState...")
    public void someProviderState() {
        LOGGER.info("SomeProviderState callback");
    }

    @PactVerifyProvider("USER_CREATED_EVENT")
    public String verifyUserCreatedEvent() {
        return new UserCreated(ID, NICKNAME).toJson();
    }

}
