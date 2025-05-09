package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.jsonMapping.*;

import java.util.Properties;
import java.util.UUID;

public class KafkaProducerService {
    private final static String TOPIC = "game-requests";
    private final Producer<String, String> producer;

    public KafkaProducerService() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.50.15.52:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    public void sendNewGame(UUID gameId, String player1, String player2, String client1, String client2) {
        try {
            GameAction gameAction = new GameAction(GameActionType.newGame);
            gameAction.setGameId(gameId);
            gameAction.setPlayer1(new Player(player1));
            gameAction.setPlayer2(new Player(player2));
            gameAction.setClient1(new Client(client1));
            gameAction.setClient2(new Client(client2));
            // JSON-Nachricht für das neue Spiel direkt erstellen
            String json = gameAction.toJSON();
            // Nachricht an Kafka senden
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "", json);
            producer.send(record);
            System.out.println("New game sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMove(UUID gameId, PlayerType player, int column) {
        try {
            GameAction gameAction = new GameAction(GameActionType.move);
            gameAction.setGameId(gameId);
            gameAction.setPlayer(player);
            gameAction.setColumn(column);
            // JSON-Nachricht für einen Spielzug direkt erstellen
            String json = gameAction.toJSON();
            // Nachricht an Kafka senden
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, gameId.toString(), json);
            producer.send(record);
            System.out.println("Move sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
