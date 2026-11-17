package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    @NotBlank(message = "질문 메시지는 필수 입력 항목이며, 공백일 수 없습니다.")
    private String message;

    private boolean isQuickReplies;

    private String quickRepliesCategory;

    private List<ChatHistoryDto> history;
}
