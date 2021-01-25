package org.camunda.bpm.engine.grpc.client.service.impl;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.CompleteRequest;
import org.camunda.bpm.engine.grpc.CompleteResponse;
import org.camunda.bpm.engine.grpc.CreateProcessInstanceRequest;
import org.camunda.bpm.engine.grpc.DeleteProcessInstanceRequest;
import org.camunda.bpm.engine.grpc.DeleteProcessInstanceResponse;
import org.camunda.bpm.engine.grpc.ExtendLockRequest;
import org.camunda.bpm.engine.grpc.ExtendLockResponse;
import org.camunda.bpm.engine.grpc.HandleBpmnErrorRequest;
import org.camunda.bpm.engine.grpc.HandleBpmnErrorResponse;
import org.camunda.bpm.engine.grpc.HandleFailureRequest;
import org.camunda.bpm.engine.grpc.HandleFailureResponse;
import org.camunda.bpm.engine.grpc.UnlockRequest;
import org.camunda.bpm.engine.grpc.UnlockResponse;
import org.camunda.bpm.engine.grpc.client.channel.Stub;
import org.camunda.bpm.engine.grpc.client.domain.Error;
import org.camunda.bpm.engine.grpc.client.domain.ExternalTask;
import org.camunda.bpm.engine.grpc.client.domain.Failure;
import org.camunda.bpm.engine.grpc.client.domain.Variables;
import org.camunda.bpm.engine.grpc.client.service.ExternalTaskService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalTaskServiceImpl implements ExternalTaskService {

    private final Stub stub;

    @Override
    public void unlock(ExternalTask externalTask) {
        UnlockRequest request = UnlockRequest.newBuilder()
            .setId(externalTask.getId())
            .build();

        stub.getStub().unlock(
            request,
            this.<UnlockResponse>createLoggingObserver(
                response -> "Task " + request.getId() + " unlocked with status " + response.getStatus(),
                "Could not unlock the task " + request.getId() + " (server error)"
            )
        );
    }

    @Override
    public void complete(ExternalTask externalTask) {
        CompleteRequest request = CompleteRequest.newBuilder()
            .setProcessInstanceId(externalTask.getProcessInstanceId().getValue())
            .setWorkerId(externalTask.getWorkerId())
            .setId(externalTask.getId())
            .build();

        stub.getStub().complete(request, this.<CompleteResponse>createLoggingObserver(
            response -> "Task " + request.getId() + " completed with status " + response.getStatus(),
            "Could not complete the task " + request.getId() + " (server error)"));
    }

    @Override
    public void complete(ExternalTask externalTask, Variables variables) {
        CompleteRequest request = CompleteRequest.newBuilder()
            .setProcessInstanceId(externalTask.getProcessInstanceId().getValue())
            .setWorkerId(externalTask.getWorkerId())
            .setId(externalTask.getId())
            .setVariables(variables.getValue())
            .build();

        stub.getStub().complete(request, this.<CompleteResponse>createLoggingObserver(
            response -> "Task " + request.getId() + " completed with status " + response.getStatus(),
            "Could not complete the task " + request.getId() + " (server error)"));
    }

    @Override
    public void handleFailure(ExternalTask externalTask, Failure failure, int retries, long retryTimeout) {
        HandleFailureRequest request = HandleFailureRequest.newBuilder()
            .setId(externalTask.getId())
            .setProcessInstanceId(externalTask.getProcessInstanceId().getValue())
            .setWorkerId(externalTask.getWorkerId())
            .setErrorMessage(failure.getMessage())
            .setErrorDetails(failure.getDetails())
            .setRetries(retries)
            .setRetryTimeout(retryTimeout)
            .build();

        stub.getStub().handleFailure(request, this.<HandleFailureResponse>createLoggingObserver(
            response -> "Handle failure for Task " + request.getId() + " with status " + response.getStatus(),
            "Could not handle the failure for the task " + request.getId() + " (server error)"));
    }

    @Override
    public void handleBpmnError(ExternalTask externalTask, Error error) {
        HandleBpmnErrorRequest request = HandleBpmnErrorRequest.newBuilder()
            .setId(externalTask.getId())
            .setProcessInstanceId(externalTask.getProcessInstanceId().getValue())
            .setWorkerId(externalTask.getWorkerId())
            .setErrorCode(error.getCode())
            .setErrorMessage(error.getMessage())
            .build();

        stub.getStub().handleBpmnError(request, this.<HandleBpmnErrorResponse>createLoggingObserver(
            response -> "BPMN Error for Task " + request.getId() + " handled with status " + response.getStatus(),
            "Could not handle the BPMN Error for the task " + request.getId() + " (server error)"));
    }

    @Override
    public void handleBpmnError(ExternalTask externalTask, Error error, Variables variables) {
        HandleBpmnErrorRequest request = HandleBpmnErrorRequest.newBuilder()
            .setId(externalTask.getId())
            .setProcessInstanceId(externalTask.getProcessInstanceId().getValue())
            .setWorkerId(externalTask.getWorkerId())
            .setErrorCode(error.getCode())
            .setErrorMessage(error.getMessage())
            .setVariables(variables.getValue())
            .build();

        stub.getStub().handleBpmnError(request, this.<HandleBpmnErrorResponse>createLoggingObserver(
            response -> "BPMN Error for Task " + request.getId() + " handled with status " + response.getStatus(),
            "Could not handle the BPMN Error for the task " + request.getId() + " (server error)"));
    }

    @Override
    public void extendLock(ExternalTask externalTask, long newDuration) {
        ExtendLockRequest request = ExtendLockRequest.newBuilder()
            .setId(externalTask.getId())
            .setWorkerId(externalTask.getWorkerId())
            .setDuration(newDuration)
            .build();

        stub.getStub().extendLock(request, this.<ExtendLockResponse>createLoggingObserver(
            response -> "Lock for Task " + request.getId() + " extended with status " + response.getStatus(),
            "Could not extend the lock for the task " + request.getId() + " (server error)"));
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String reason) {
        DeleteProcessInstanceRequest request = DeleteProcessInstanceRequest.newBuilder()
            .setProcessInstanceId(processInstanceId)
            .setReason(reason)
            .build();

        stub.getStub().deleteProcessInstance(request, this.<DeleteProcessInstanceResponse>createLoggingObserver(
            response -> "Delete process instance " + request.getProcessInstanceId() + " complete with status " + response.getStatus(),
            "Could not delete process instance " + request.getProcessInstanceId() + " (server error)"));
    }

    @Override
    public void createProcessInstance(String processName, Variables variables) {
        CreateProcessInstanceRequest request = CreateProcessInstanceRequest.newBuilder()
            .setProcessName(processName)
            .setVariables(variables.getValue())
            .build();

        stub.getStub().createProcessInstance(request, this.createLoggingObserver(
            response -> "Create process instance " + request.getProcessName() + " complete with status " + response.getStatus(),
            "Could not create process instance " + request.getProcessName() + " (server error)"));
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
