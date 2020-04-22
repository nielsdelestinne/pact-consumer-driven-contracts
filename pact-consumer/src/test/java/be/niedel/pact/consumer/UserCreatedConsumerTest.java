package be.niedel.pact.consumer;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserProvider", providerType = ProviderType.ASYNCH)
class UserCreatedConsumerTest {

    private static final String ID = "4de42227-d5a3-4a22-993f-dec0cfdaffed";
    protected static final String NICKNAME = "jimmy_drop_tables";

    @Pact(provider="UserProvider", consumer="UserConsumer")
    MessagePact createUserEvent(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringValue("id", ID);
        body.stringValue("nickNameForUser", NICKNAME);

        return builder.given("SomeProviderState...")
                .expectsToReceive("USER_CREATED_EVENT")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createUserEvent")
    void test(List<Message> messages) {
        final String userEventAsJson = new String(messages.get(0).contentsAsBytes());
        assertThat(userEventAsJson).contains("\"id\":\"" + ID + "\"");
        assertThat(userEventAsJson).contains("\"nickNameForUser\":\"" + NICKNAME + "\"");
    }



}
