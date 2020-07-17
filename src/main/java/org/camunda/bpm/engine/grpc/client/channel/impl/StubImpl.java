package org.camunda.bpm.engine.grpc.client.channel.impl;

import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.ExternalTaskGrpc;
import org.camunda.bpm.engine.grpc.ExternalTaskGrpc.ExternalTaskStub;
import org.camunda.bpm.engine.grpc.client.channel.Channel;
import org.camunda.bpm.engine.grpc.client.channel.Stub;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StubImpl implements Stub {

    private final Channel channel;

    private final List<ClientInterceptor> clientInterceptors;

    private ExternalTaskStub stub;

    @Override
    public ExternalTaskStub getStub() {
        if (stub == null) {
            stub = buildStub();
        }

        return stub;
    }

    private ExternalTaskStub buildStub() {
        log.info("Building new GRPC stub for channel {}", channel.getChannel());

        return ExternalTaskGrpc.newStub(
            channel.getChannel()
        ).withInterceptors(clientInterceptors.toArray(new ClientInterceptor[0]));
    }
}
