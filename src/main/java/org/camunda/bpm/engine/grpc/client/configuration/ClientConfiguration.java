package org.camunda.bpm.engine.grpc.client.configuration;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.camunda.bpm.engine.grpc.client.channel.impl.ChannelImpl;
import org.camunda.bpm.engine.grpc.client.request.RequestFactory;
import org.camunda.bpm.engine.grpc.client.request.impl.RequestFactoryImpl;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.camunda.bpm.engine.grpc.client.subscription.impl.AbstractSubscriptionHandler;
import org.camunda.bpm.engine.grpc.client.subscription.impl.SubscriptionHandlerParameters;
import org.camunda.bpm.engine.grpc.client.subscription.impl.SubscriptionImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan("org.camunda.bpm.engine.grpc.client")
@RequiredArgsConstructor
public class ClientConfiguration {

    private final ApplicationContext applicationContext;

    private final SubscriptionRepository subscriptionRepository;

    @Bean
    RequestFactory requestFactory(
        SubscriptionRepository subscriptionRepository,
        ClientConfigurationProperties properties
    ) {
        return new RequestFactoryImpl(
            subscriptionRepository,
            properties.getWorkerId(),
            properties.getUsePriority(),
            properties.getLockDuration()
        );
    }

    @Bean
    Channel channel(ClientConfigurationProperties properties) {
        return new ChannelImpl(properties.getAddress());
    }

    @Bean
    SubscriptionHandlerParameters subscriptionHandlerParameters(ClientConfigurationProperties properties) {
        return new SubscriptionHandlerParameters(
            properties.getRetryCount(),
            properties.getRetryTimeout(),
            properties.getAsync(),
            properties.getErrorKey()
        );
    }

    @PostConstruct
    void registerSubscriptions() {
        applicationContext.getBeansOfType(AbstractSubscriptionHandler.class)
            .values()
            .forEach(handler -> {
                subscriptionRepository.add(
                    SubscriptionImpl.builder()
                        .topicName(handler.getTopicName())
                        .handler(handler)
                        .build()
                );
            });
    }
}
