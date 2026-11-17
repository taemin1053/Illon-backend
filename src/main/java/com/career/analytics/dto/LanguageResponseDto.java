package com.career.analytics.dto;

import com.career.analytics.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class LanguageResponseDto {

    private final Long id;

    // ✨ 1. 이제는 userId가 아니라 resumeId의 시대입니다.
    private final Long resumeId;

    private final String languageName;
    private final String testName;
    private final String score;
    private final LocalDate testDate;

    // ✨ 2. 기획서 스펙 반영: 상세 내용 필드 추가
    private final String description;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static LanguageResponseDto from(Language language) {
        return LanguageResponseDto.builder()
                .id(language.getId())
                // ✨ 3. 빨간 줄 해결: getUser() 대신 getResume() 사용
                .resumeId(language.getResume() != null ? language.getResume().getId() : null)
                .languageName(language.getLanguageName())
                .testName(language.getTestName())
                .score(language.getScore())
                .testDate(language.getTestDate())
                // ✨ 4. 엔티티에서 상세 내용 꺼내서 담기
                .description(language.getDescription())
                .createdAt(language.getCreatedAt())
                .updatedAt(language.getUpdatedAt())
                .build();
    }
}