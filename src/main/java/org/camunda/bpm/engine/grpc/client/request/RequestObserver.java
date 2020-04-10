package org.camunda.bpm.engine.grpc.client.request;

import io.grpc.stub.StreamObserver;
import org.camunda.bpm.engine.grpc.FetchAndLockRequest;

public interface RequestObserver {

    StreamObserver<FetchAndLockRequest> getStreamObserver();
}
