package org.camunda.bpm.engine.grpc.client.worker;

public interface Handler {

    void handle();

    void complete();
}
