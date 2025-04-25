import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import quickfix.*;

@Slf4j
@MessageEndpoint
public class FixApplication implements quickfix.Application {

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("FIX session created: {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("FIX session logged on: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("FIX session logged out: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log.info("Outgoing admin message: {}", message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("Incoming admin message: {}", message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log.info("Outgoing app message: {}", message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("Incoming app message: {}", message);
        processMessage(message, sessionId);
    }

    @ServiceActivator(inputChannel = "fixInboundChannel")
    public void processMessage(Message message, SessionID sessionId) throws FieldNotFound {
        // Process different message types
        String msgType = message.getHeader().getString(quickfix.field.MsgType.FIELD);
        
        switch (msgType) {
            case quickfix.field.MsgType.ORDER_SINGLE:
                processNewOrderSingle(message, sessionId);
                break;
            case quickfix.field.MsgType.EXECUTION_REPORT:
                processExecutionReport(message, sessionId);
                break;
            default:
                log.warn("Unhandled message type: {}", msgType);
        }
    }

    private void processNewOrderSingle(Message message, SessionID sessionId) throws FieldNotFound {
        quickfix.fix42.NewOrderSingle nos = new quickfix.fix42.NewOrderSingle();
        message.copyTo(nos);
        
        log.info("Processing NewOrderSingle: ClOrdID={}, Symbol={}, Side={}, OrderQty={}, Price={}",
                nos.getClOrdID().getValue(),
                nos.getSymbol().getValue(),
                nos.getSide().getValue(),
                nos.getOrderQty().getValue(),
                nos.getPrice().getValue());
        
        // Business logic for order processing
    }

    private void processExecutionReport(Message message, SessionID sessionId) throws FieldNotFound {
        quickfix.fix42.ExecutionReport er = new quickfix.fix42.ExecutionReport();
        message.copyTo(er);
        
        log.info("Processing ExecutionReport: OrderID={}, ExecID={}, ExecType={}, OrdStatus={}",
                er.getOrderID().getValue(),
                er.getExecID().getValue(),
                er.getExecType().getValue(),
                er.getOrdStatus().getValue());
        
        // Business logic for execution reports
    }
}