public class IncomingChatMessage {
    private String sender;
    private String text;
    private String clientId;
    private String topic;

    public IncomingChatMessage() {}

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
