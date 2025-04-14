import java.time.LocalDateTime;
// Die Klasse ClientStatus repräsentiert den aktuellen Status eines Clients

public class ClientStatus {
    private String status;

    // Konstruktor
    public ClientStatus(String status) {
        this.status = status;
    }

    // Getter und Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Überschreibt die Standardausgabe (z.B. für System.out.println)
    @Override
    public String toString() {
        return "ClientStatus{" +
                "status='" + status + '\'' +
                '}';
    }
}
