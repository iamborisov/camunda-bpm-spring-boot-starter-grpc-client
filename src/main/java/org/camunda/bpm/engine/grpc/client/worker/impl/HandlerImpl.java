package org.camunda.bpm.engine.grpc.client.worker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.request.RequestFactory;
import org.camunda.bpm.engine.grpc.client.request.RequestObserver;
import org.camunda.bpm.engine.grpc.client.subscription.Subscription;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.camunda.bpm.engine.grpc.client.worker.Handler;
import org.camunda.bpm.engine.grpc.client.worker.Watchdog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerImpl implements Handler {

    private final Watchdog watchdog;

    private final SubscriptionRepository subscriptionRepository;

    private final RequestObserver requestObserver;

    private final RequestFactory requestFactory;

    private Collection<Subscription> lastSubscriptionsState = new ArrayList<>();

    @Override
    public void handle() {
        if (!handleConnection() || !handleSubscriptions()) {
            sleep();
        }
    }

    @Override
    public void complete() {
        requestObserver.getStreamObserver().onCompleted();
    }

    private boolean handleConnection() {
        if (!watchdog.watch()) {
            requestObserver.reset();
            lastSubscriptionsState.clear();

            return false;
        }

        return true;
    }

    private boolean handleSubscriptions() {
        if (subscriptionRepository.isEmpty()) {
            log.info("There is no registered external task subscriptions");

            return false;
        }

        if (subscriptionRepository.get().equals(lastSubscriptionsState)) {
            log.info("There is no changes in subscriptions since last call");

            return false;
        }

        requestObserver.getStreamObserver().onNext(
            requestFactory.create()
        );

        lastSubscriptionsState = subscriptionRepository.get();

        log.info(
            "Subscriptions updated. Now subscribed on topics: {}",
            subscriptionRepository.get()
                .stream()
                .map(Subscription::getTopicName)
                .collect(Collectors.joining(", "))
        );

        return true;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("Thread sleep interrupted", e);
        }
    }
}
