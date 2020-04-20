package be.niedel.pact.consumer.domain;

import java.util.UUID;

public class UserCreated implements DomainEvent {

    private UUID id;
    private String nickname;

    public UserCreated() {
    }

    public UserCreated(UUID id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }
}
