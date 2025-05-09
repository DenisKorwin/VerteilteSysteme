package org.example.jsonMapping;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameProposal {
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private UUID gameId;
    private Player player1;
    private Client client1;

    @JsonIgnore
    public static GameProposal fromJSON(String json) {
        try {
            return objectMapper.readValue(json, GameProposal.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public String toJSON() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @JsonCreator
    public GameProposal(@JsonProperty("gameId") UUID gameId, @JsonProperty("player1") Player player1, @JsonProperty("client1") Client client1) {
    	this.gameId = gameId;
    	this.player1 = player1;
    	this.client1 = client1;
    }
    
    public UUID getGameId() {
    	return gameId;
    }
    
    public Player getPlayer1() {
    	return player1;
    }
    
    public Client getClient1() {
    	return client1;
    }
}
