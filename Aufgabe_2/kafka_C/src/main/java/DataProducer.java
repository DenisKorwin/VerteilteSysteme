import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Properties;

import java.util.Properties;

// Kafka Producer der kontinuierlich die CPU-Last abruft und diese als Nachrichten an ein Kafka-Topic sendet
public class DataProducer {
    public static void main(String[] args) {
        String topic = "vlvs_inf22b_DKBM";
        String bootstrapServer = "10.50.15.52:9092";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            while (true) {
                double cpuLoad = osBean.getSystemLoadAverage();  // CPU-Last holen
                String message = "CPU Last: " + cpuLoad;
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, "key", message);

                // CPU Auslastung als Nachricht an Kafka senden
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            System.out.println("Fehler beim senden der Nachricht: " + exception.getMessage());
                        } else {
                            System.out.println("Nachricht wurde an das Topic " + metadata.topic() +
                                    " in Partition " + metadata.partition() +
                                    " mit Offset " + metadata.offset() + " gesendet.");
                        }
                    }
                });
                // Eine kleine Pause, um die Daten nicht zu schnell zu senden
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}