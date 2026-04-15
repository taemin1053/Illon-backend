package com.career.analytics.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CertificationRequestDto {

    @NotBlank(message = "자격증명은 필수입니다")
    @Size(max = 100, message = "자격증명은 100자 이내여야 합니다")
    private String certificationName;

    @Size(max = 100, message = "발급기관은 100자 이내여야 합니다")
    private String issuingOrganization;

    private LocalDate issueDate; // 취득일

    private LocalDate expiryDate; // 만료일 (없으면 null)
}