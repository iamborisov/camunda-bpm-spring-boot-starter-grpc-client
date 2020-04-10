package org.camunda.bpm.engine.grpc.client.service.impl;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.CreateMessageRequest;
import org.camunda.bpm.engine.grpc.CreateMessageResponse;
import org.camunda.bpm.engine.grpc.client.channel.Stub;
import org.camunda.bpm.engine.grpc.client.domain.BusinessKey;
import org.camunda.bpm.engine.grpc.client.domain.ProcessInstanceId;
import org.camunda.bpm.engine.grpc.client.domain.Variables;
import org.camunda.bpm.engine.grpc.client.service.MessageCorrelationService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCorrelationServiceImpl implements MessageCorrelationService {

    private final Stub stub;

    @Override
    public void createMessage(String messageName) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setMessageName(messageName)
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, ProcessInstanceId processInstanceId) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setProcessInstanceId(processInstanceId.getValue())
                .setMessageName(messageName)
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, ProcessInstanceId processInstanceId, Variables variables) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setProcessInstanceId(processInstanceId.getValue())
                .setMessageName(messageName)
                .setVariables(variables.getValue())
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, ProcessInstanceId processInstanceId, Variables variables, BusinessKey businessKey) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setProcessInstanceId(processInstanceId.getValue())
                .setMessageName(messageName)
                .setVariables(variables.getValue())
                .setBusinessKey(businessKey.getValue())
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, ProcessInstanceId processInstanceId, BusinessKey businessKey) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setProcessInstanceId(processInstanceId.getValue())
                .setMessageName(messageName)
                .setBusinessKey(businessKey.getValue())
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, Variables variables) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setMessageName(messageName)
                .setVariables(variables.getValue())
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, Variables variables, BusinessKey businessKey) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setMessageName(messageName)
                .setVariables(variables.getValue())
                .setBusinessKey(businessKey.getValue())
                .build()
        );
    }

    @Override
    public void createMessage(String messageName, BusinessKey businessKey) {
        sendMessage(
            CreateMessageRequest.newBuilder()
                .setMessageName(messageName)
                .setBusinessKey(businessKey.getValue())
                .build()
        );
    }

    private void sendMessage(CreateMessageRequest request) {
        stub.getStub().createMessage(request, this.<CreateMessageResponse>createLoggingObserver(
            response -> "Successful for Message " + request.getMessageName() + " with process instance id " + request.getProcessInstanceId(),
            "Could not create message correlation " + request.getMessageName() + " (server error)"));
    }

    private <T> StreamObserver<T> createLoggingObserver(Function<T, String> succesMessageFunction, String errorMessage) {
        return new StreamObserver<T>() {
            @Override
            public void onNext(T response) {
                log.info(succesMessageFunction.apply(response));
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(errorMessage, throwable);
            }

            @Override
            public void onCompleted() {
                // nothing to do
            }
        };
    }
}
