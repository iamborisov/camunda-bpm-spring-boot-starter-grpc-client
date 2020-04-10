package org.camunda.bpm.engine.grpc.client.service;

import org.camunda.bpm.engine.grpc.client.domain.Error;
import org.camunda.bpm.engine.grpc.client.domain.ExternalTask;
import org.camunda.bpm.engine.grpc.client.domain.Failure;
import org.camunda.bpm.engine.grpc.client.domain.Variables;

public interface ExternalTaskService {

    void unlock(ExternalTask externalTask);

    void complete(ExternalTask externalTask);

    void complete(ExternalTask externalTask, Variables variables);

    void handleFailure(ExternalTask externalTask, Failure failure, int retries, long retryTimeout);

    void handleBpmnError(ExternalTask externalTask, Error error);

    void handleBpmnError(ExternalTask externalTask, Error error, Variables variables);

    void extendLock(ExternalTask externalTask, long newDuration);
}
