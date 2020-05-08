package org.camunda.bpm.engine.grpc.client.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.camunda.bpm.engine.grpc.client.channel.impl.ChannelImpl;
import org.camunda.bpm.engine.grpc.client.request.RequestFactory;
import org.camunda.bpm.engine.grpc.client.request.impl.RequestFactoryImpl;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.camunda.bpm.engine.grpc.client.subscription.impl.AbstractSubscriptionHandler;
import org.camunda.bpm.engine.grpc.client.subscription.impl.SubscriptionHandlerParameters;
import org.camunda.bpm.engine.grpc.client.subscription.impl.SubscriptionImpl;
import org.camunda.bpm.engine.grpc.client.worker.Worker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Slf4j
@Configuration
@ComponentScan("org.camunda.bpm.engine.grpc.client")
@RequiredArgsConstructor
public class ClientConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Bean
    RequestFactory requestFactory(
        SubscriptionRepository subscriptionRepository,
        ClientConfigurationProperties properties
    ) {
        return new RequestFactoryImpl(
            subscriptionRepository,
            properties.getWorkerId(),
            properties.getUsePriority(),
            properties.getLockDuration(),
            properties.getMaxTasks()
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

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribeTaskHandlers(
            event.getApplicationContext().getBeansOfType(AbstractSubscriptionHandler.class).values(),
            event.getApplicationContext().getBean(SubscriptionRepository.class)
        );

        connect(
            event.getApplicationContext().getBean(ClientConfigurationProperties.class),
            event.getApplicationContext().getBean(Worker.class)
        );
    }

    private void subscribeTaskHandlers(Collection<AbstractSubscriptionHandler> handlers, SubscriptionRepository subscriptionRepository) {
        handlers.forEach(handler -> {
            subscriptionRepository.add(
                SubscriptionImpl.builder()
                    .topicName(handler.getTopicName())
                    .handler(handler)
                    .build()
            );
        });
    }

    private void connect(ClientConfigurationProperties configurationProperties, Worker worker) {
        if (configurationProperties.getAutoStart()) {
            try {
                worker.start();
            } catch (Worker.AlreadyStartedException e) {
                log.error("Worker already started", e);
            }
        }
    }
}