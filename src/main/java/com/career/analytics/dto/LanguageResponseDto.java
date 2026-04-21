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
    private final Long userId;
    private final String languageName;
    private final String testName;
    private final String score;
    private final LocalDate testDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static LanguageResponseDto from(Language language) {
        return LanguageResponseDto.builder()
                .id(language.getId())
                .userId(language.getUser() != null ? language.getUser().getId() : null)
                .languageName(language.getLanguageName())
                .testName(language.getTestName())
                .score(language.getScore())
                .testDate(language.getTestDate())
                .createdAt(language.getCreatedAt())
                .updatedAt(language.getUpdatedAt())
                .build();
    }
}
