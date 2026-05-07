package com.career.analytics.dto;

import com.career.analytics.entity.CoverLetter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CoverLetterResponseDto {
    private Long id;
    // ✨ 1. 이력서 고유 ID 추가!
    private Long resumeId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CoverLetterResponseDto from(CoverLetter coverLetter) {
        return CoverLetterResponseDto
                .builder()
                .id(coverLetter.getId())
                // ✨ 2. 엔티티에서 이력서 ID 꺼내서 담기!
                .resumeId(coverLetter.getResume().getId())
                .content(coverLetter.getContent())
                .createdAt(coverLetter.getCreatedAt())
                .updatedAt(coverLetter.getUpdatedAt())
                .build();
    }
}