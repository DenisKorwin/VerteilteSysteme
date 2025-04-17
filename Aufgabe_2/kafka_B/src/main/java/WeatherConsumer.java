import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class WeatherConsumer {
    public static void main(String[] args) {
        String topic = "weather";
        String bootstrapServer = "10.50.15.52:9092";
        String groupId = "weather-consumer-group";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            boolean keepConsuming = true;
            while(keepConsuming) {
                final ConsumerRecords<String,String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String,String> record : consumerRecords) {
                    parseAndDisplayWeatherData(record.value());
                }
            }
        }
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