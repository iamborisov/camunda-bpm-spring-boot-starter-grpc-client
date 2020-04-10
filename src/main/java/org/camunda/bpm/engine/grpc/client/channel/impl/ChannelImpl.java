package org.camunda.bpm.engine.grpc.client.channel.impl;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelImpl implements Channel {

    private final String address;

    private ManagedChannel channel;

    @Override
    public ManagedChannel getChannel() {
        if (channel == null) {
            channel = buildChannel();
        }

        return channel;
    }

    private ManagedChannel buildChannel() {
        log.info("Building new channel to {}", address);

        return NettyChannelBuilder
            .forTarget(address)
            .keepAliveWithoutCalls(true)
            .keepAliveTime(10, TimeUnit.SECONDS)
            .keepAliveTimeout(60, TimeUnit.SECONDS)
            .idleTimeout(5, TimeUnit.MINUTES)
            .enableRetry()
            .maxRetryAttempts(2)
            .negotiationType(NegotiationType.PLAINTEXT)
            .usePlaintext()
            .defaultServiceConfig(getServiceConfig())
            .build();
    }

    private Map<String, Object> getServiceConfig() {
        Map<String, Object> name = Map.ofEntries(
            Map.entry("service", "org.camunda.bpm.engine.grpc.ExternalTask")
        );

        Map<String, Object> methodConfig = Map.ofEntries(
            Map.entry("name", Collections.<Object>singletonList(name)),
            Map.entry("retryPolicy", getRetryConfig())
        );

        return Map.ofEntries(
            Map.entry("methodConfig", Collections.<Object>singletonList(methodConfig))
        );
    }

    private Map<String, Object> getRetryConfig() {
        return Map.ofEntries(
            Map.entry("maxAttempts", 2D),
            Map.entry("initialBackoff", "1s"),
            Map.entry("maxBackoff", "30s"),
            Map.entry("backoffMultiplier", 2D),
            Map.entry("retryableStatusCodes", Arrays.<Object>asList(
                "CANCELLED",
                "UNKNOWN",
                "DEADLINE_EXCEEDED",
                "ABORTED",
                "INTERNAL",
                "DATA_LOSS",
                "UNAVAILABLE"
            ))
        );
    }
}
