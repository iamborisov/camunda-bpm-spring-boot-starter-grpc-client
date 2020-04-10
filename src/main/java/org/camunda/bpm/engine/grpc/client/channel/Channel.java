package org.camunda.bpm.engine.grpc.client.channel;

import io.grpc.ManagedChannel;

public interface Channel {

    ManagedChannel getChannel();
}
