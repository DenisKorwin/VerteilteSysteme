package org.example.jsonMapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GameAction {
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final GameActionType type;
    private UUID gameId;
    private PlayerType player;
    private Player player1;
    private Player player2;
    private Client client1;
    private Client client2;
    private int column;
    private int row;
    private int rows;
    private int columns;

    @JsonIgnore
    public static GameAction fromJSON(String json) {
        try {
            return objectMapper.readValue(json, GameAction.class);
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
    public GameAction(@JsonProperty("type") GameActionType type) {
        this.type = type;
    }

    public GameActionType getType() {
        return type;
    }

    public UUID getGameId() {
        return gameId;
    }

    public PlayerType getPlayer() {
        return player;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Client getClient1() {
        return client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void setPlayer(PlayerType player) {
        this.player = player;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }
}
