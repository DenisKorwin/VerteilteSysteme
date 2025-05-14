package org.example;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.gui.MediationFrame;
import org.example.jsonMapping.Client;
import org.example.jsonMapping.GameProposal;
import org.example.jsonMapping.Player;
import org.example.jsonMapping.PlayerType;

public class GameMediator {
	private final GameManager gameManager;
	
	private final static String TOPIC = "game-mediation";
    private final Producer<String, String> producer;
    private final Consumer<String, String> consumer;

    public GameMediator(GameManager gameManager) {
    	this.gameManager = gameManager;
    	
        Properties pProps = new Properties();
        pProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.50.15.52:9092");
        pProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        pProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(pProps);
        
        Properties cProps = new Properties();
        cProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.50.15.52:9092");
        cProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        cProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        consumer = new KafkaConsumer<>(cProps);
        consumer.subscribe(Collections.singletonList(TOPIC));
        new MediationFrame(this);
    }

    // endet einen Spielvorschlag (Spiel-ID, Spieler 1, Client 1) an Kafka und lässt den Spieler auf den Spielstart warten.
    public void proposeGame(UUID gameId, Player player1, Client client1) {
    	String json = new GameProposal(gameId, player1, client1).toJSON();
    	ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "", json);
    
    	producer.send(record);
    	System.out.println("New game proposed: " + json);
    	
    	gameManager.waitForGame(gameId, player1, client1);
    }

    // Wartet auf einen eingehenden Spielvorschlag von einem anderen Spieler und startet das Spiel, sobald ein Vorschlag empfangen wurde.
    public void waitForProposal(Player player2, Client client2) {
    	boolean running = true;
    	while(running) {
    		ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
    		
    		for(ConsumerRecord<String, String> record : records) {
    			GameProposal gameProposal = GameProposal.fromJSON(record.value());
    			if(gameProposal == null)
    				continue;
    			running = false;
    			gameManager.beginGame(gameProposal.getGameId(), gameProposal.getPlayer1(), gameProposal.getClient1(), player2, client2);
    		}
    	}
    }
}
