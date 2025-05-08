package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.jsonMapping.GameAction;
import org.example.jsonMapping.GameEvent;
import org.example.jsonMapping.GameEventState;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaConsumerService implements Runnable {

    private final Consumer<String, String> consumer;
    private UUID gameId;
    private GameManager gameManager;

    // Konstruktor, der das GameModel initialisiert und Kafka-Consumer konfiguriert
    public KafkaConsumerService(UUID gameId, GameManager gameManager) {
        this.gameId = gameId;
        this.gameManager = gameManager;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.50.15.52:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "linetris-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("game-events")); // Auf das 'game-events' Topic abonnieren
    }

    @Override
    public void run() {
        listen(gameId);
    }

    // Methode zum Empfangen und Verarbeiten der Nachrichten
    public void listen(UUID gameId) {
        while (true) {
            // Kafka-Nachrichten mit einer Zeitspanne von 500 ms abfragen
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            // Jede empfangene Nachricht verarbeiten
            for (ConsumerRecord<String, String> record : records) {
                String jsonData = record.value();
                System.out.println("Event empfangen: " + jsonData);

                // Verarbeite das empfangene Ereignis
                parseAndApplyGameEvent(jsonData, gameId);
            }
        }
    }

    // Methode zum Verarbeiten der empfangenen Nachrichten
    private void parseAndApplyGameEvent(String jsonData, UUID gameId) {
        GameEvent gameEvent = GameEvent.fromJSON(jsonData);
        if(gameEvent == null)
            return;
        if(gameEvent.getGameId() != gameId)
            return;
        if(gameEvent.getState() != GameEventState.OK) {
            if (gameEvent.getMessage() != null)
                System.out.println(gameEvent.getMessage());
            return;
        }
        for(GameAction gameAction : gameEvent.getActions()) {
            switch(gameAction.getType()) {
                case move:
                    gameManager.receiveMove(gameAction);
                    break;
                case deleteBottomRow:
                    gameManager.receiveDeleteBottomRow(gameAction);
                    break;
                case winAction:
                    gameManager.receiveWinAction(gameAction);
                    break;
                case newGame:
                    gameManager.receiveNewGame(gameAction);
                    break;
            }
        }
    }

    // Methode zum Schließen des Consumers
    public void close() {
        consumer.close();
    }
}
