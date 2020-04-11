package org.camunda.bpm.engine.grpc.client.subscription.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHandlerParameters {

    Integer retryCount = 5;

    Long retryTimeout = 1000L;

    Boolean async = true;

    String errorKey = "error";
}
