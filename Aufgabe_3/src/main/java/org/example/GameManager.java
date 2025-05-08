package org.example;

import org.example.gui.Frame;
import org.example.jsonMapping.GameAction;
import org.example.jsonMapping.GameEvent;
import org.example.jsonMapping.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class GameManager {
    private final Frame frame;
    private KafkaConsumerService consumerService;
    private final KafkaProducerService producerService;

    private final UUID gameId = UUID.fromString("56b71776-490f-48e5-b60f-17dd28904230");
    private PlayerType player = PlayerType.PLAYER1;
    private String player1 = "Denis";
    private String player2 = "Player2";
    private String client1 = "Client1";
    private String client2 = "Client2";

    //columns, then rows
    private PlayerType[][] board;

    private Thread consumerThread;

    public static void main(String[] args) {
        new GameManager();
    }

    public GameManager() {
        frame = new Frame();
        producerService = new KafkaProducerService();

        producerService.sendNewGame(gameId, player1, player2, client1, client2);

        consumerService = new KafkaConsumerService(gameId, this);
        consumerThread = new Thread(consumerService);
        consumerThread.start();

    }

    private void updateFrame() {
        for(int column = 0; column < board.length; column++) {
            for(int row = 0; row < board[0].length; row++) {
                Color color;
                switch(board[column][row]) {
                    case PLAYER1 -> color = new Color(255, 0, 0);
                    case PLAYER2 -> color = new Color(0, 0, 255);
                    default -> color = new Color(255, 255, 255);
                }
                frame.updateButton(column, row, color);
            }
        }
    }

    public void sendMove(int column) {
        producerService.sendMove(gameId, player, column);
    }

    public void receiveMove(GameAction gameAction) {
        for(int row = 0; row < board[gameAction.getColumn()].length; row++) {
            if(board[gameAction.getColumn()][row] != null)
                continue;
            board[gameAction.getColumn()][row] = gameAction.getPlayer();
            updateFrame();
            return;
        }
    }

    public void receiveDeleteBottomRow(GameAction gameAction) {
        for(int column = 0; column < board.length; column++) {
            for(int row = 0; row < board[0].length - 1; row++) {
                board[column][row] = board[column][row + 1];
            }
            board[column][board[0].length - 1] = null;
        }
        updateFrame();
    }

    public void receiveWinAction(GameAction gameAction) {
        if(gameAction.getPlayer() == this.player) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Du hast das Spiel gewonnen.",
                    "Spiel gewonnen!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Du hast das Spiel verloren.",
                    "Spiel verloren...",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        try {
            consumerThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void receiveNewGame(GameAction gameAction) {
        board = new PlayerType[gameAction.getCols()][gameAction.getRows()];
        frame.setDimensions(board.length, board[0].length);
        updateFrame();
    }
}
