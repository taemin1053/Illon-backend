package com.career.analytics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiChatResponseDto {

    private String answer;

    private List<Object> sources;

    private String intent;

    @JsonProperty("user_message")
    private Object userMessage;

    @JsonProperty("assistant_message")
    private Object assistantMessage;
}
