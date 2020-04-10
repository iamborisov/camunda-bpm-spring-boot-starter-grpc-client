package org.camunda.bpm.engine.grpc.client.domain;

import lombok.Value;

@Value
public class Error {

    String code;

    String message;
}
