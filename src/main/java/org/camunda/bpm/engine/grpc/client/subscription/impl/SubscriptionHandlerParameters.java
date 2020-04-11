package org.camunda.bpm.engine.grpc.client.subscription.impl;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SubscriptionHandlerParameters {

    Integer retryCount;

    Long retryTimeout;

    Boolean async;

    String errorKey;
}
