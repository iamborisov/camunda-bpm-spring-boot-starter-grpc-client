package org.camunda.bpm.engine.grpc.client.worker;

public interface Worker extends Runnable {

    void start() throws AlreadyStartedException;

    void stop() throws AlreadyStoppedException;

    Boolean isRunning();

    class StateChangeException extends Exception {
    }

    class AlreadyStartedException extends StateChangeException {
    }

    class AlreadyStoppedException extends StateChangeException {
    }
}
