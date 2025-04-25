import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;
import quickfix.Message;
import quickfix.SessionID;

@MessagingGateway
public interface FixGateway {
    @Gateway(requestChannel = "fixOutboundChannel")
    void send(Message fixMessage);
}

public class FixOutboundGateway {

    private final Initiator initiator;

    public FixOutboundGateway(Initiator initiator) {
        this.initiator = initiator;
    }

    public void send(quickfix.Message fixMessage) {
        try {
            SessionID sessionId = initiator.getSessions().get(0);
            quickfix.Session.sendToTarget(fixMessage, sessionId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send FIX message", e);
        }
    }
}