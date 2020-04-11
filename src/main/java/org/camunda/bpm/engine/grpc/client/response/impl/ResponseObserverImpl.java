package org.camunda.bpm.engine.grpc.client.response.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.FetchAndLockResponse;
import org.camunda.bpm.engine.grpc.client.domain.BusinessKey;
import org.camunda.bpm.engine.grpc.client.domain.ExternalTask;
import org.camunda.bpm.engine.grpc.client.domain.ProcessInstanceId;
import org.camunda.bpm.engine.grpc.client.domain.Variables;
import org.camunda.bpm.engine.grpc.client.response.ResponseObserver;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.camunda.bpm.engine.grpc.client.worker.Locker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseObserverImpl implements ResponseObserver {

    private final Locker locker;

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void onNext(FetchAndLockResponse response) {
        log.info("Server response acquired");

        try {
            subscriptionRepository.get()
                .stream()
                .filter(
                    subscription ->
                        subscription.getTopicName().equals(response.getTopicName())
                )
                .forEach(
                    subscription ->
                        subscription.getHandler().handle(
                            buildExternalTask(response)
                        )
                );
        } catch (Exception e) {
            log.error("Exception while handling task {}", response.getTopicName(), e);
        }

        locker.unlock();
    }

    @Override
    public void onError(Throwable t) {
        log.error("Exception on server side", t);

        locker.unlock();
    }

    @Override
    public void onCompleted() {
        log.info("Server response handling completed");

        locker.unlock();
    }

    private ExternalTask buildExternalTask(FetchAndLockResponse response) {
        return ExternalTask.builder()
            .id(response.getId())
            .workerId(response.getWorkerId())
            .topicName(response.getTopicName())
            .retries(response.getRetries())
            .variables(new Variables(response.getVariables()))
            .businessKey(new BusinessKey(response.getBusinessKey()))
            .processInstanceId(new ProcessInstanceId(response.getProcessInstanceId()))
            .build();
    }
}
