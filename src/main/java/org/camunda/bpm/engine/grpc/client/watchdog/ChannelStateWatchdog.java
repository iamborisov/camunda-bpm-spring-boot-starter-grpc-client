package org.camunda.bpm.engine.grpc.client.watchdog;

import io.grpc.ConnectivityState;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class ChannelStateWatchdog implements Runnable {

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private final Thread thread = new Thread(this, getClass().getSimpleName());

    private Long timeout = 5_000L;

    private Channel channel;

    public ChannelStateWatchdog(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        synchronized(thread) {
            while (isRunning.get()) {
                try {
                    checkChannelState();

                    thread.wait(timeout);
                } catch (Throwable e) {
                    log.error("Exception while monitoring channel state", e);
                }
            }
        }
    }

    private void checkChannelState() {
        if (channel != null) {
            ConnectivityState state = channel.getChannel().getState(true);

            log.info("Watchdog reports channel state '{}'", state.toString());
        }
    }

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            log.info("Starting channel state watchdog");

            thread.start();
        }
    }

    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Channel state watchdog thread interrupted", e);
            }
        }
    }
}