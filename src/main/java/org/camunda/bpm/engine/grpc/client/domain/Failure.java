package org.camunda.bpm.engine.grpc.client.domain;

import lombok.Value;

@Value
public class Failure {

    String message;

    String details;
}
