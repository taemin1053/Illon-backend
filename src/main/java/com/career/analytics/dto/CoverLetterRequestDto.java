package com.career.analytics.dto;

import com.career.analytics.entity.CoverLetter;
import com.career.analytics.entity.Resume;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverLetterRequestDto {
    @NotBlank(message = "자기소개서 내용을 입력해주세요.")
    private String content;

    public CoverLetter toEntity(Resume resume) {
        return CoverLetter.builder()
                .resume(resume)
                .content(this.content)
                .build();
    }
}
