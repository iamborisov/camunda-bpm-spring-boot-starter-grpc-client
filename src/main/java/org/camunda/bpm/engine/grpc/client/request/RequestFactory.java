package org.camunda.bpm.engine.grpc.client.request;

import org.camunda.bpm.engine.grpc.FetchAndLockRequest;

public interface RequestFactory {

    FetchAndLockRequest create();
}
