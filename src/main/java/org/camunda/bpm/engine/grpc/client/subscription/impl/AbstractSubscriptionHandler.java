package org.camunda.bpm.engine.grpc.client.subscription.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.grpc.client.domain.Error;
import org.camunda.bpm.engine.grpc.client.domain.ExternalTask;
import org.camunda.bpm.engine.grpc.client.domain.Failure;
import org.camunda.bpm.engine.grpc.client.domain.Variables;
import org.camunda.bpm.engine.grpc.client.service.ExternalTaskService;
import org.camunda.bpm.engine.grpc.client.subscription.SubscriptionHandler;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
abstract public class AbstractSubscriptionHandler<T> implements SubscriptionHandler {

    private final ObjectMapper objectMapper;

    private final SubscriptionHandlerParameters subscriptionHandlerParameters;

    private final ExternalTaskService externalTaskService;

    @Override
    public void handle(ExternalTask externalTask) {
        if (subscriptionHandlerParameters.getAsync()) {
            CompletableFuture.runAsync(() -> doHandle(externalTask));
        } else {
            doHandle(externalTask);
        }
    }

    private void doHandle(ExternalTask externalTask) {
        log.info("Start processing an external task with external id '{}' business key '{}' from the topic '{}'",
            externalTask.getProcessInstanceId(),
            externalTask.getBusinessKey(),
            getTopicName());

        final Class<T> taskParametersClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        T parameters = null;
        final String variablesJsonValue = externalTask.getVariables().getValue();

        try {
            parameters = objectMapper.readValue(variablesJsonValue, taskParametersClass);
        } catch (JsonProcessingException e) {
            log.error("Unable to transform '{}' to '{}' from topic '{}'", variablesJsonValue, taskParametersClass, getTopicName(), e);
        }

        final DataBinder dataBinder = new DataBinder(parameters);
        dataBinder.addValidators(new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return taskParametersClass.equals(clazz);
            }

            @Override
            public void validate(Object target, Errors errors) {
                AbstractSubscriptionHandler.this.validate((T) target, errors);
            }
        });
        dataBinder.validate();

        try {
            if (dataBinder.getBindingResult().hasErrors()) {
                log.warn(
                    "Parameters validation error while trying to handle '{}' from topic {}",
                    getTopicName(),
                    dataBinder.getBindingResult().getAllErrors().toString()
                );

                Map<String, Object> variables = Map.of(
                    subscriptionHandlerParameters.getErrorKey(),
                    dataBinder.getBindingResult().getAllErrors()
                );

                externalTaskService.handleBpmnError(
                    externalTask,
                    new Error(
                        "Validation exception",
                        dataBinder.getBindingResult().getAllErrors().toString()
                    ),
                    new Variables(
                        getMergedVariables(variablesJsonValue, variables)
                    )
                );

                return;
            }

            Map<String, Object> result = handle(parameters, externalTask);

            externalTaskService.complete(
                externalTask,
                new Variables(getMergedVariables(variablesJsonValue, result))
            );
        } catch (BusinessLogicException e) {
            handleBusinessLogicException(e, externalTask, externalTaskService);
        } catch (Throwable t) {
            handleFatalException(t, externalTask, externalTaskService);
        }
    }

    private void handleBusinessLogicException(BusinessLogicException e, ExternalTask externalTask, ExternalTaskService externalTaskService) {
        try {
            log.warn(
                "Business logic exception occurred while handling task from topic '{}' '{}'",
                getTopicName(),
                e.getMessage(),
                e
            );

            Map<String, Object> variables = new HashMap<>();
            variables.put(
                subscriptionHandlerParameters.getErrorKey(),
                e
            );

            externalTaskService.handleBpmnError(
                externalTask,
                new Error(
                    e.getClass().toString(),
                    e.getMessage()
                ),
                new Variables(
                    getMergedVariables(externalTask.getVariables().getValue(), variables)
                )
            );
        } catch (Throwable t) {
            handleFatalException(t, externalTask, externalTaskService);
        }
    }

    private void handleFatalException(Throwable t, ExternalTask externalTask, ExternalTaskService externalTaskService) {
        log.error(
            "Fatal exception occurred while handling from topic '{}' '{}'",
            getTopicName(),
            t.getMessage(),
            t
        );

        externalTaskService.handleFailure(
            externalTask,
            new Failure(
                t.getClass().toString(),
                t.getMessage()
            ),
            getRetryCount(externalTask),
            getRetryTimeout()
        );
    }

    protected Integer getRetryCount(ExternalTask externalTask) {
        return externalTask.getRetries() == -1
            ? subscriptionHandlerParameters.getRetryCount()
            : externalTask.getRetries() - 1;
    }

    protected Long getRetryTimeout() {
        return subscriptionHandlerParameters.getRetryTimeout();
    }

    abstract public String getTopicName();

    abstract protected void validate(T parameters, Errors errors);

    abstract protected Map<String, Object> handle(T parameters, ExternalTask externalTask) throws Throwable;

    private String getMergedVariables(String jsonValue, Map<String, Object> variables)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(
            getResultVariables(jsonValue, variables)
        );
    }

    private Object getResultVariables(String jsonValue, Map<String, Object> result)
        throws JsonProcessingException {
        Map variables = objectMapper.readValue(jsonValue, Map.class);

        Map<String, Object> resultMap = result.entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                Map.Entry::getValue));

        return Stream.concat(variables.entrySet().stream(), resultMap.entrySet().stream())
            .collect(toMap(
                (Map.Entry e) -> e.getKey(),
                (Map.Entry e) -> e.getValue(),
                (v1, v2) -> v2));
    }
}
