package be.niedel.pact.consumer.projection;

import java.util.UUID;

public class UserCreated implements DomainEvent {

    private UUID id;
    private String nickname;

    public UserCreated() {
    }

    public UserCreated(String id, String nickname) {
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
