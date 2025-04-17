package org.Weather;

import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(20));

            for(ConsumerRecord<String, String> record : records) {
                parseAndDisplayWeatherData(record.value());
            }
        }
    }
    private static void sendToGraphite(String metricPath, double value, long timestamp) {
        try (Socket socket = new Socket("10.50.15.52", 2003);
             OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {

            socket.setSoTimeout(5000);  // Timeout auf 5 Sekunden setzen
            
            String message = metricPath + " " + value + " " + timestamp + "\n";
            writer.write(message);
            writer.flush();

            System.out.println("Sent to Graphite: " + metricPath + " = " + value + " at " + timestamp);

        } catch (IOException e) {
            System.err.println("Failed to send metric to Graphite: " + e.getMessage());
        }
    }

    // Methode zum Parsen und Anzeigen von empfangenen Wetterdaten im JSON-Format
    private static void parseAndDisplayWeatherData(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WeatherData weatherData = objectMapper.readValue(jsonData, WeatherData.class);
            System.out.println("Weather Data: " + weatherData);


            // Zeitstempel für Graphite (Unix Timestamp)
            long timestamp = Instant.parse(weatherData.getTimeStamp()).getEpochSecond();

            // An Graphite senden
            sendToGraphite("test.inf22.testgruppe1." + weatherData.getCity().replace(" ", ""), weatherData.getTempCurrent(), timestamp);

        } catch (Exception e) {
            System.err.println("Failed to parse weather data: " + e.getMessage());
        }
    }

}



