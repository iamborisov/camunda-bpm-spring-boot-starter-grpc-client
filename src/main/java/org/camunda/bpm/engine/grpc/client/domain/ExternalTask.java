package org.camunda.bpm.engine.grpc.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ExternalTask {

    String id;

    String workerId;

    String topicName;

    Integer retries;

    Variables variables;

    BusinessKey businessKey;

    ProcessInstanceId processInstanceId;
}
