package org.example.jsonMapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

public class GameEvent {
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final long timeStamp;
    private List<GameAction> actions;
    private GameEventState state;
    private UUID gameId;
    private String message;

    @JsonIgnore
    public static GameEvent fromJSON(String json) {
        try {
            return objectMapper.readValue(json, GameEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonCreator
    public GameEvent(@JsonProperty("timeStamp") long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public List<GameAction> getActions() {
        return actions;
    }

    public GameEventState getState() {
        return state;
    }

    public UUID getGameId() {
        return gameId;
    }

    public String getMessage() {
        return message;
    }

    public void setActions(List<GameAction> actions) {
        this.actions = actions;
    }

    public void setState(GameEventState state) {
        this.state = state;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
