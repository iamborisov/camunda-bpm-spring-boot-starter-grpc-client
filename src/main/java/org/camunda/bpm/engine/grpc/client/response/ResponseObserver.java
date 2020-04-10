package org.camunda.bpm.engine.grpc.client.response;

import io.grpc.stub.StreamObserver;
import org.camunda.bpm.engine.grpc.FetchAndLockResponse;

public interface ResponseObserver extends StreamObserver<FetchAndLockResponse> {
}
