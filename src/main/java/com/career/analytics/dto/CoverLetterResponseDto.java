package com.career.analytics.dto;

import com.career.analytics.entity.CoverLetter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CoverLetterResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CoverLetterResponseDto from(CoverLetter coverLetter) {
        return CoverLetterResponseDto
                .builder()
                .id(coverLetter.getId())
                .content(coverLetter.getContent())
                .createdAt(coverLetter.getCreatedAt())
                .updatedAt(coverLetter.getUpdatedAt())
                .build();
    }
}
