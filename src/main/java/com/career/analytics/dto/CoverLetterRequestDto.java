package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverLetterRequestDto {
    @NotBlank(message = "자기소개서 내용을 입력해주세요.")
    private String content;
}
