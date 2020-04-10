package org.camunda.bpm.engine.grpc.client.subscription.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.subscription.Subscription;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private Collection<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    @Override
    public void add(Subscription subscription) {
        log.info("Subscription to topic {} added", subscription.getTopicName());

        subscriptions.add(subscription);
    }

    @Override
    public void remove(Subscription subscription) {
        subscriptions.remove(subscription);

        log.info("Subscription to topic {} removed", subscription.getTopicName());
    }

    @Override
    public void clear() {
        subscriptions.clear();

        log.info("Removed all subscriptions");
    }

    @Override
    public Boolean isEmpty() {
        return subscriptions.isEmpty();
    }

    @Override
    public Collection<Subscription> get() {
        return new ArrayList<>(subscriptions);
    }
}
