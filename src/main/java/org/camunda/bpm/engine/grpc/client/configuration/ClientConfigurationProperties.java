package org.camunda.bpm.engine.grpc.client.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "grpc")
public class ClientConfigurationProperties {

    private String address;

    private String workerId;

    private Boolean usePriority = true;

    private Long lockDuration = 5000L;

    private Boolean autoStart = true;

    // TODO: implement channel settings

    private Boolean keepAliveWithoutCalls;

    private Long keepAliveTime;

    private Long keepAliveTimeout;

    private Long idleTimeout;

    // TODO: move AbstractSubscription properties to separated configuration class

    private Integer retryCount = 5;

    private Long retryTimeout = 1000L;

    private Boolean async = true;

    private String errorKey = "error";
}
