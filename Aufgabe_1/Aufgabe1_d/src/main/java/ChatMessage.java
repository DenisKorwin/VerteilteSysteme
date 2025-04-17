// Diese Klasse repräsentiert eine Chat-Nachricht, die über MQTT gesendet oder empfangen wird
public class ChatMessage {
    private String sender;
    private String clientId;
    private String text;
    private String topic;

    public ChatMessage() {} // Für Jackson

    // Konstruktor mit allen Attributen
    public ChatMessage(String sender, String clientId, String topic, String message) {
        this.sender = sender;
        this.clientId = clientId;
        this.text = message;
        this.topic = topic;
    }

    // Getter und Setter – notwendig für die Serialisierung/Deserialisierung
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getText() { return text; }
    public void setText(String message) { this.text = message; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}
