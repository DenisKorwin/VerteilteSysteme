import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.time.LocalDateTime;
import java.util.Scanner;


public class ChatClient {

    private static final String BROKER_ADDRESS = "tcp://10.50.12.150:1883";
    private static final String TOPIC = "/aichat/default";
    private static final String clientId = "f1a2168b-4f0c-4da5-9219-7b5702de57c3";
    private static String sender;
    private static Mqtt5Client client = null;
    // Stellt eine Verbindung zum MQTT-Broker her, Abonniert das Topic und startet den Chat.
    public static void main(String[] args) {

        try {
            client = Mqtt5Client.builder()
                    .identifier("Client")
                    .serverHost("10.50.12.150")
                    .serverPort(1883)
                    .willPublish()
                    .topic("/aichat/clientstate")
                    .payload(new ObjectMapper().writeValueAsBytes(new ClientStatus("Chat Client disconnected unexpectedly")))
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .retain(true)
                    .applyWillPublish()
                    .build();
        }catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        client.toAsync().connectWith()
                .simpleAuth()
                .username("TestUser")
                .password("TestPasswd".getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Connection failed: " + throwable.getMessage());
                        return;
                    }
                    System.out.println("Connected to broker");

                    // Statusnachricht senden
                    publishClientStatus(client, "Chat Client started");

                    client.toAsync().subscribeWith()
                            .topicFilter(TOPIC)
                            .callback(publish -> {
                                String payload = new String(publish.getPayloadAsBytes());
                                //hierbei handelt es sich um die Json
                                //System.out.println("Received message: " + payload);
                                parseAndDisplayChatData(payload);
                            })
                            .send()
                            .whenComplete((subAck, subThrowable) -> {
                                if (subThrowable != null) {
                                    System.err.println("Subscription failed: " + subThrowable.getMessage());
                                } else {
                                    System.out.println("Subscribed to topic: " + TOPIC);
                                }
                            });
                    //chatclient starten
                    startChat(client);
                });
    }
    // sendet eine Chatnachricht als JSON
    private static void sendMessage(Mqtt5Client client, String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Die Nachricht wird als JSON formatiert
            String jsonPayload = objectMapper.writeValueAsString(new ChatMessage("Sender", clientId, TOPIC, message));

            client.toAsync().publishWith()
                    .topic(TOPIC)
                    .payload(jsonPayload.getBytes())
                    .send();
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    // Startet eine Eingabeschleife
    private static void startChat(Mqtt5Client client) {
        Scanner scanner = new Scanner(System.in);
        String message;

        // Endlosschleife für die Chat-Nachrichten
        while (true) {
            message = scanner.nextLine();
            // Beendet die Anwendung, wenn "exit" eingegeben wird
            if (message.equalsIgnoreCase("exit")) {
                // Wenn der Benutzer "exit" eingibt, das Programm beenden
                publishClientStatus(client, "Chat Client stopped");
                System.out.println("Chat Client stopped.");
                client.toAsync().disconnect();
                break;
            }

            // Sende die Nachricht an das Topic
            sendMessage(client, message);
        }
    }

    // Verarbeitung empfangener Json-Nachrichten
    private static void parseAndDisplayChatData(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IncomingChatMessage msg = objectMapper.readValue(jsonData, IncomingChatMessage.class);
            System.out.println(msg.getSender() + ": " + msg.getText());
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der Nachricht: " + e.getMessage());
        }
    }
    // Sendet eine Statusmeldung des Clients
    private static void publishClientStatus(Mqtt5Client client, String status) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClientStatus clientStatus = new ClientStatus(status);

            // Die Statusnachricht wird als JSON formatiert
            String jsonPayload = objectMapper.writeValueAsString(clientStatus);

            client.toAsync().publishWith()
                    .topic("/aichat/clientstate")
                    .payload(jsonPayload.getBytes())
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .retain(true)
                    .send();
        } catch (Exception e) {
            System.err.println("Failed to publish client status: " + e.getMessage());
        }
    }
}
