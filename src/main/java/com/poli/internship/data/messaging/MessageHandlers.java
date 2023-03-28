package com.poli.internship.data.messaging;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlers {
    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputChannel")
    public MessageHandler userCreationMessageSender(PubSubTemplate pubSubTemplate){
        return new PubSubMessageHandler(pubSubTemplate, "curriculum-authorization");
    }
}
