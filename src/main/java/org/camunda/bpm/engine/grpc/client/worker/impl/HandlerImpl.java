package org.camunda.bpm.engine.grpc.client.worker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.request.RequestFactory;
import org.camunda.bpm.engine.grpc.client.request.RequestObserver;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.camunda.bpm.engine.grpc.client.worker.Handler;
import org.camunda.bpm.engine.grpc.client.worker.Locker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerImpl implements Handler {

    private final Locker locker;

    private final SubscriptionRepository subscriptionRepository;

    private final RequestObserver requestObserver;

    private final RequestFactory requestFactory;

    @Override
    public void handle() {
        if (!subscriptionRepository.isEmpty()) {
            requestObserver.getStreamObserver().onNext(
                requestFactory.create()
            );

            locker.lock();
        } else {
            log.info("There is no registered external task subscriptions");
        }
    }

    @Override
    public void complete() {
        requestObserver.getStreamObserver().onCompleted();
    }
}
