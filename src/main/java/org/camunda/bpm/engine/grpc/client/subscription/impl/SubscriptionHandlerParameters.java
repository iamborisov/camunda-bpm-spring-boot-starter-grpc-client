package org.camunda.bpm.engine.grpc.client.subscription.impl;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SubscriptionHandlerParameters {

    Integer retryCount = 5;

    Long retryTimeout = 1000L;

    Boolean async = true;

    String errorKey = "error";
}
