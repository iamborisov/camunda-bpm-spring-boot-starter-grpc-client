package org.camunda.bpm.engine.grpc.client.request.impl;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.FetchAndLockRequest;
import org.camunda.bpm.engine.grpc.client.channel.Stub;
import org.camunda.bpm.engine.grpc.client.request.RequestObserver;
import org.camunda.bpm.engine.grpc.client.response.ResponseObserver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestObserverImpl implements RequestObserver {

    private final Stub stub;

    private final ResponseObserver responseObserver;

    private StreamObserver<FetchAndLockRequest> streamObserver;

    @Override
    public StreamObserver<FetchAndLockRequest> getStreamObserver() {
        if (streamObserver == null) {
            streamObserver = buildStreamObserver();
        }

        return streamObserver;
    }

    private StreamObserver<FetchAndLockRequest> buildStreamObserver() {
        return stub.getStub().fetchAndLock(responseObserver);
    }

    @Override
    public void reset() {
        streamObserver = null;
    }
}
