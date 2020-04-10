package org.camunda.bpm.engine.grpc.client.worker;

public interface Locker {

    void lock();

    void unlock();
}
