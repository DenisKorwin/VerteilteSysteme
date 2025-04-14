import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherClient {

    // Adresse des MQTT-Brokers (hier lokal oder im Netzwerk erreichbar)
    private static final String BROKER_ADDRESS = "tcp://10.50.12.150:1883";
    // MQTT-Topic, auf das wir uns abonnieren wollen
    private static final String TOPIC = "/weather/mosbach";

    public static void main(String[] args) {
        // Erstellen eines neuen MQTT v5 Clients mit spezifischer Client-ID und Serverdaten
        Mqtt5Client client = Mqtt5Client.builder()
                .identifier("weatherClient")                 // Eindeutige Client-ID
                .serverHost("10.50.12.150")                 // IP-Adresse des Brokers
                .serverPort(1883)                           // Standardport für MQTT (unverschlüsselt)
                .build();

        // Asynchrone Verbindung mit dem Broker inklusive Authentifizierung
        client.toAsync().connectWith()
                .simpleAuth()
                .username("TestUser")                       // Benutzername für Authentifizierung
                .password("TestPasswd".getBytes())         // Passwort als Byte-Array
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        // Fehler beim Verbindungsaufbau
                        System.err.println("Connection failed: " + throwable.getMessage());
                        return;
                    }
                    System.out.println("Connected to broker");

                    // Nach erfolgreicher Verbindung: Abonniere das Wetter-Topic
                    client.toAsync().subscribeWith()
                            .topicFilter(TOPIC)            // Das Topic, auf das gehört wird
                            .callback(publish -> {
                                // Wird bei jeder neuen Nachricht aufgerufen
                                String payload = new String(publish.getPayloadAsBytes());
                                parseAndDisplayWeatherData(payload);  // JSON wird verarbeitet
                            })
                            .send()
                            .whenComplete((subAck, subThrowable) -> {
                                if (subThrowable != null) {
                                    // Fehler beim Abonnieren des Topics
                                    System.err.println("Subscription failed: " + subThrowable.getMessage());
                                } else {
                                    System.out.println("Subscribed to topic: " + TOPIC);
                                }
                            });
                });
    }

    // Methode zum Parsen und Anzeigen von empfangenen Wetterdaten im JSON-Format
    private static void parseAndDisplayWeatherData(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WeatherData weatherData = objectMapper.readValue(jsonData, WeatherData.class);
            System.out.println("Weather Data: " + weatherData);
        } catch (Exception e) {
            System.err.println("Failed to parse weather data: " + e.getMessage());
        }
    }
}