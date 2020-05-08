package org.camunda.bpm.engine.grpc.client.worker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.worker.Handler;
import org.camunda.bpm.engine.grpc.client.worker.Watchdog;
import org.camunda.bpm.engine.grpc.client.worker.Worker;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerImpl implements Worker {

    private final Handler handler;

    private final Watchdog watchdog;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private Thread thread = new Thread(this, getClass().getSimpleName());

    @Override
    public void start() throws AlreadyStartedException {
        if (isRunning.compareAndSet(false, true)) {
            log.info("Starting worker thread");

            thread.start();
        } else {
            throw new AlreadyStartedException();
        }
    }

    @Override
    public void stop() throws AlreadyStoppedException {
        if (isRunning.compareAndSet(true, false)) {
            try {
                log.info("Stopping worker thread");

                thread.join();
            } catch (InterruptedException e) {
                log.error("Exception while stopping worker thread", e);

                Thread.currentThread().interrupt();
            }
        } else {
            throw new AlreadyStoppedException();
        }
    }

    @Override
    public void run() {
        while (isRunning.get()) {
            try {
                watchdog.watch();
                handler.handle();
            } catch (Throwable e) {
                log.error("Exception while handling worker thread", e);
            }
        }

        handler.complete();
    }

    @Override
    public Boolean isRunning() {
        return isRunning.get();
    }
}
