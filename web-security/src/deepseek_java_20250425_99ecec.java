import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final FixGateway fixGateway;

    @PostMapping
    public String createOrder() {
        NewOrderSingle order = new NewOrderSingle(
                new ClOrdID("ORD" + System.currentTimeMillis()),
                new HandlInst('1'),
                new Symbol("AAPL"),
                new Side(Side.BUY),
                new TransactTime(new java.util.Date()),
                new OrdType(OrdType.LIMIT)
        );
        
        order.set(new OrderQty(100));
        order.set(new Price(150.25));
        
        fixGateway.send(order);
        
        return "Order sent via FIX";
    }
}