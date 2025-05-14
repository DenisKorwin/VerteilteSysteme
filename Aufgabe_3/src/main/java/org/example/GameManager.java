package org.example;

import org.example.gui.Frame;
import org.example.jsonMapping.*;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.UUID;

public class GameManager {
    private Frame frame;
    private KafkaConsumerService consumerService;
    private KafkaProducerService producerService;

    private UUID gameId = UUID.fromString("56b71776-490f-48e5-b60f-17dd28904262");
    private PlayerType player = PlayerType.PLAYER1;
    private String player1 = "Denis";
    private String player2 = "Manuela";
    private String client1 = "Client1";
    private String client2 = "Client2";

    //columns, then rows
    private PlayerType[][] board;

    private Thread consumerThread;

    public static void main(String[] args) {
        new GameManager();
    }

    // Konstruktor – startet die Spielvermittlung über den GameMediator
    public GameManager() {
        new GameMediator(this);
    }

    // Startet ein neues Spiel und initialisiert GUI, Producer und Consumer
    private void startGame() {
        frame = new Frame(this);
        producerService = new KafkaProducerService();

        consumerService = new KafkaConsumerService(gameId, this);
        consumerThread = new Thread(consumerService);
        consumerThread.start();

    }

    // Wartet auf den Spielstart nach eigenem Vorschlag (Spieler 1)
    public void waitForGame(UUID gameId, Player player1, Client client1) {
        this.gameId = gameId;
        this.player1 = player1.getName();
        this.client1 = client1.getName();
        this.player = PlayerType.PLAYER1;
        startGame();
    }

    // Beginnt ein Spiel nach Empfang eines Vorschlags (Spieler 2) und sendet Start-Nachricht
    public void beginGame(UUID gameId, Player player1, Client client1, Player player2, Client client2) {
        this.gameId = gameId;
        this.player = PlayerType.PLAYER2;
        this.player1 = player1.getName();
        this.client1 = client1.getName();
        this.player2 = player2.getName();
        this.client2 = client2.getName();
        startGame();
        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producerService.sendNewGame(gameId, this.player1, this.player2, this.client1, this.client2);
    }

    // Aktualisiert das GUI (Spielfeldanzeige) basierend auf dem internen Spielzustand
    private void updateFrame() {
        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length; row++) {
                Color color;
                if (board[column][row] == null) {
                    color = Color.WHITE;
                } else if (board[column][row] == PlayerType.PLAYER1) {
                    color = Color.RED;
                } else {
                    color = Color.BLUE;
                }
                frame.updateButton(column, row, color);
            }
        }
    }

    // Sendet einen Spielzug (Spalte) an den Kafka-Producer
    public void sendMove(int column) {
        producerService.sendMove(gameId, player, column + 1);
    }

    // Verarbeitet empfangenen Spielzug und aktualisiert das Spielfeld
    public void receiveMove(GameAction gameAction) {
        for (int row = 0; row < board[gameAction.getColumn() - 1].length; row++) {
            if (board[gameAction.getColumn() - 1][row] != null)
                continue;
            board[gameAction.getColumn() - 1][row] = gameAction.getPlayer();
            updateFrame();
            return;
        }
    }

    // Entfernt die unterste Reihe im Spielfeld (nach entsprechender Aktion)
    public void receiveDeleteBottomRow(GameAction gameAction) {
        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length - 1; row++) {
                board[column][row] = board[column][row + 1];
            }
            board[column][board[0].length - 1] = null;
        }
        updateFrame();
    }

    // Zeigt eine Info-Nachricht im GUI an, falls sie den lokalen Spieler betrifft
    public void receiveInfo(GameEvent gameEvent) {
        if (gameEvent.getActions().get(0).getPlayer() == this.player) {
            JOptionPane.showMessageDialog(
                    frame,
                    gameEvent.getMessage(),
                    "INFO",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Verarbeitet das Spielergebnis (Sieg oder Niederlage) und beendet das Spiel
    public void receiveWinAction(GameAction gameAction) {
        if (gameAction.getPlayer() == this.player) {
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

        consumerService.stop();
        try {
            consumerThread.interrupt();
            consumerThread.join();
        } catch (InterruptedException e) {
        }
        System.exit(0);
    }

    // Initialisiert das Spielfeld mit gegebener Größe beim Spielstart
    public void receiveNewGame(GameAction gameAction) {
        board = new PlayerType[gameAction.getCols()][gameAction.getRows()];
        frame.setDimensions(board.length, board[0].length);
        updateFrame();
    }
}
