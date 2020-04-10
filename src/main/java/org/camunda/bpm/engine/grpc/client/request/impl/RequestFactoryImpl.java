package org.camunda.bpm.engine.grpc.client.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.FetchAndLockRequest;
import org.camunda.bpm.engine.grpc.FetchAndLockRequest.FetchExternalTaskTopic;
import org.camunda.bpm.engine.grpc.client.request.RequestFactory;
import org.camunda.bpm.engine.grpc.client.subscription.Subscription;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestFactoryImpl implements RequestFactory {

    private final SubscriptionRepository subscriptionRepository;

    private final String workerId;

    private final Boolean usePriority;

    private final Long lockDuration;

    @Override
    public FetchAndLockRequest create() {
        return FetchAndLockRequest.newBuilder()
            .setWorkerId(workerId)
            .setUsePriority(usePriority)
            .addAllTopic(
                subscriptionRepository.get()
                    .stream()
                    .map(this::buildTopic)
                    .collect(Collectors.toUnmodifiableList())
            )
            .build();
    }

    private FetchExternalTaskTopic buildTopic(Subscription subscription) {
        FetchExternalTaskTopic.Builder topicRequestDto = FetchExternalTaskTopic.newBuilder()
            .setTopicName(subscription.getTopicName())
            .setLockDuration(
                subscription.getLockDuration() != null
                    ? subscription.getLockDuration()
                    : lockDuration
            );

        if (subscription.getBusinessKey() != null && notEmpty(subscription.getBusinessKey().getValue())) {
            topicRequestDto.setBusinessKey(subscription.getBusinessKey().getValue());
        }

        if (notEmpty(subscription.getProcessDefinitionId())) {
            topicRequestDto.setProcessDefinitionId(subscription.getProcessDefinitionId());
        }

        if (notEmpty(subscription.getProcessDefinitionIdIn())) {
            topicRequestDto.addAllProcessDefinitionIdIn(subscription.getProcessDefinitionIdIn());
        }

        if (notEmpty(subscription.getProcessDefinitionKey())) {
            topicRequestDto.setProcessDefinitionKey(subscription.getProcessDefinitionKey());
        }

        if (notEmpty(subscription.getProcessDefinitionKeyIn())) {
            topicRequestDto.addAllProcessDefinitionKeyIn(subscription.getProcessDefinitionKeyIn());
        }

        if (subscription.isWithoutTenantId()) {
            topicRequestDto.setWithoutTenantId(subscription.isWithoutTenantId());
        }

        if (notEmpty(subscription.getTenantIdIn())) {
            topicRequestDto.addAllTenantIdIn(subscription.getTenantIdIn());
        }

        if (notEmpty(subscription.getProcessDefinitionVersionTag())) {
            topicRequestDto.setProcessDefinitionVersionTag(subscription.getProcessDefinitionVersionTag());
        }

        return topicRequestDto.build();
    }

    private static boolean notEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private static boolean notEmpty(Collection<?> list) {
        return list != null && !list.isEmpty();
    }
}
