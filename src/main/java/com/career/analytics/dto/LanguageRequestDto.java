package com.career.analytics.dto;

import com.career.analytics.entity.Language;
import com.career.analytics.entity.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Size(max = 50, message = "시험명 50자 이내로 입력해주세요.")
    private String testName;

    @Size(max = 50, message = "점수는 50자 이내 입력해주세요.")
    private String score;

    @NotNull(message = "시험일을 선택해주세요.")
    private LocalDate testDate;

    // ✨ 기획서 스펙 반영: 상세 내용(description) 필드 추가!
    @Size(max = 2000, message = "상세 내용은 2000자 이내여야 합니다.")
    private String description;

    public Language toEntity(Resume resume) {
        return Language.builder()
                .resume(resume)
                .languageName(this.languageName)
                .testName(this.testName)
                .score(this.score)
                .testDate(this.testDate)
                .description(this.description)
                .build();
    }
}