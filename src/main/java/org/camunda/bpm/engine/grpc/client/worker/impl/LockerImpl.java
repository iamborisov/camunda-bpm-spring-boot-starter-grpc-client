package org.camunda.bpm.engine.grpc.client.worker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.worker.Locker;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockerImpl implements Locker {

    private Semaphore semaphore = new Semaphore(0);

    @Override
    public void lock() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error("Client was stopped while waiting on answer from server", e);
        }
    }

    @Override
    public void unlock() {
        semaphore.release();
    }
}
