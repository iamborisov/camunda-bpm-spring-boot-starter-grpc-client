package org.camunda.bpm.engine.grpc.client.service;

import org.camunda.bpm.engine.grpc.client.domain.BusinessKey;
import org.camunda.bpm.engine.grpc.client.domain.ProcessInstanceId;
import org.camunda.bpm.engine.grpc.client.domain.Variables;

public interface MessageCorrelationService {

    void createMessage(String messageName);

    void createMessage(String messageName, ProcessInstanceId processInstanceId);

    void createMessage(String messageName, ProcessInstanceId processInstanceId, Variables variables);

    void createMessage(String messageName, ProcessInstanceId processInstanceId, Variables variables, BusinessKey businessKey);

    void createMessage(String messageName, ProcessInstanceId processInstanceId, BusinessKey businessKey);

    void createMessage(String messageName, Variables variables);

    void createMessage(String messageName, Variables variables, BusinessKey businessKey);

    void createMessage(String messageName, BusinessKey businessKey);
}
