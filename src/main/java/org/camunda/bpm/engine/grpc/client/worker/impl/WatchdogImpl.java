package org.camunda.bpm.engine.grpc.client.worker.impl;

import io.grpc.ConnectivityState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.camunda.bpm.engine.grpc.client.worker.Watchdog;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WatchdogImpl implements Watchdog {

    private final Channel channel;

    @Override
    public void watch() {
        ConnectivityState state = channel.getChannel().getState(true);

        log.info("Watchdog reports channel state '{}'", state.toString());
    }
}
