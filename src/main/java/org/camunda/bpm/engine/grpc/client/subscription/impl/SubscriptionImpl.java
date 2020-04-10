package org.camunda.bpm.engine.grpc.client.subscription.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.grpc.client.domain.BusinessKey;
import org.camunda.bpm.engine.grpc.client.subscription.Subscription;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionHandler;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionImpl implements Subscription {

    private String topicName;

    private Long lockDuration;

    private SubscriptionHandler handler;

    private List<String> variableNames;

    private BusinessKey businessKey;

    private String processDefinitionId;

    private List<String> processDefinitionIdIn;

    private String processDefinitionKey;

    private List<String> processDefinitionKeyIn;

    private String processDefinitionVersionTag;

    private boolean withoutTenantId;

    private List<String> tenantIdIn;
}
