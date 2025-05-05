package org.Weather;

import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class WeatherConsumer {
    public static void main(String[] args) {
        String topic = "weather";
        String bootstrapServer = "10.50.15.52:9092";
        String groupId = UUID.randomUUID().toString();

        // Zeitstempel aus Datei lesen
        long lastProcessedTimestamp = readOffsetFromFile();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));


            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

                if (records.isEmpty()) {
                    // Falls keine neuen Nachrichten, 100ms warten und erneut versuchen
                    Thread.sleep(100);
                    continue;
                }


                for (ConsumerRecord<String, String> record : records) {
                    if (record.timestamp() > lastProcessedTimestamp) {
                        parseAndDisplayWeatherData(record.value());
                        lastProcessedTimestamp = record.timestamp();
                    }
                }

                saveOffsetToFile(lastProcessedTimestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sendern der Daten an Graphit
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

    // Lese den zuletzt gespeicherten Zeitstempel aus einer Datei
    private static long readOffsetFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("offset.txt"))) {
            String line = reader.readLine();
            return line != null ? Long.parseLong(line) : 0L; // Default auf 0, falls Datei leer ist
        } catch (IOException e) {
            System.err.println("Error reading offset from file: " + e.getMessage());
            return 0L; // Falls ein Fehler auftritt, starte bei 0
        }
    }

    // Speichere den aktuellen Zeitstempel in einer Datei (ab hier soll dann wieder gestartet werden)
    private static void saveOffsetToFile(long offset) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("offset.txt"))) {
            writer.write(String.valueOf(offset));
        } catch (IOException e) {
            System.err.println("Error writing offset to file: " + e.getMessage());
        }
    }
}
