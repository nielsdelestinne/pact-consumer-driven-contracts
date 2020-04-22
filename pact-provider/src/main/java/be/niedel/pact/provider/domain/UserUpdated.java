package be.niedel.pact.provider.domain;

import java.util.UUID;

public class UserUpdated implements DomainEvent {

    private UUID id;
    private String nickname;

    public UserUpdated() {
    }

    public UserUpdated(String id, String nickname) {
        this.id = UUID.fromString(id);
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String toJson() {
        return "{\"nickname\": \"" + nickname + "\",\"id\": \"" + id + "\"}";
    }

}
