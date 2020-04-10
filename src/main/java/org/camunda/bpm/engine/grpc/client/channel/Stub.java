package org.camunda.bpm.engine.grpc.client.channel;

import org.camunda.bpm.engine.grpc.ExternalTaskGrpc.ExternalTaskStub;

public interface Stub {

    ExternalTaskStub getStub();
}
