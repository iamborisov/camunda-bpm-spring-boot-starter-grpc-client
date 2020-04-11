package org.camunda.bpm.engine.grpc.client.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
public class Variables {

    String value;

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Variables from(Map<String, Object> data) throws JsonProcessingException {
        return new Variables(
            mapper.writeValueAsString(
                data.entrySet().stream()
                    .filter(v -> !Objects.isNull(v.getValue()))
                    .collect(Collectors.toMap(e -> e, Map.Entry::getValue)))
        );
    }
}
