import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.Initiator;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.spring.FixInitiatorFactoryBean;
import quickfix.spring.FixSessionSettingsLocator;

import java.io.IOException;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class FixIntegrationConfig {

    @Bean
    public SessionSettings sessionSettings() throws ConfigError, IOException {
        return new FixSessionSettingsLocator("fix-config.cfg").getObject();
    }

    @Bean
    public Initiator fixInitiator(quickfix.Application fixApplication, 
                                SessionSettings sessionSettings) throws ConfigError {
        SocketInitiator initiator = new SocketInitiator(
            fixApplication,
            new quickfix.MemoryStoreFactory(),
            sessionSettings,
            new DefaultMessageFactory()
        );
        return initiator;
    }

    @Bean
    public FixInitiatorFactoryBean fixInitiatorFactoryBean(Initiator initiator) {
        FixInitiatorFactoryBean factoryBean = new FixInitiatorFactoryBean();
        factoryBean.setInitiator(initiator);
        return factoryBean;
    }

    @Bean
    public MessageChannel fixOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel fixInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "fixOutboundChannel")
    public FixOutboundGateway fixOutboundGateway(Initiator initiator) {
        return new FixOutboundGateway(initiator);
    }
}