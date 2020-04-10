package org.camunda.bpm.engine.grpc.client.subscription;

import org.camunda.bpm.engine.grpc.client.domain.ExternalTask;

public interface SubscriptionHandler {

    void handle(ExternalTask externalTask);
}
