package com.career.analytics.dto;

import com.career.analytics.entity.Education;
import com.career.analytics.entity.EducationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EducationResponseDto {

    private Long id;
    private Long resumeId;
    private String schoolName;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private EducationStatus status;

    // ✨ 빠져있던 '이수 과목 및 연구 내용' 추가!
    private String description;

    public EducationResponseDto(Education education) {
        this.id = education.getId();
        this.resumeId = education.getResume().getId();
        this.schoolName = education.getSchoolName();
        this.major = education.getMajor();
        this.startDate = education.getStartDate();
        this.endDate = education.getEndDate();
        this.status = education.getStatus();

        // ✨ 엔티티에서 까서 DTO에 담아줍니다!
        this.description = education.getDescription();
    }
    // 기존에 만드신 생성자 바로 아래에 추가
    public static EducationResponseDto from(Education education) {
        return new EducationResponseDto(education);

    }
}