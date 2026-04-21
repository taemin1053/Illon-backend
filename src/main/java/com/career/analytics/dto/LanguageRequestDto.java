package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LanguageRequestDto {

    @NotBlank(message = "언어명은 필수 입니다.")
    @Size(max = 50, message = "언어명은 50자 이내로 입력해주세요.")
    private String languageName;

    @Size(max = 50, message = "시험명 50자 이내로 입력해주세요")
    private String testName;

    @Size(max = 50, message = "점수는 50자 이내 입력해주세요")
    private String score;

    @NotBlank(message = "시험일을 선택해주세요.")
    private LocalDate testDate;
}
