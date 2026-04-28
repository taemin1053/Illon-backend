package com.career.analytics.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AwardRequestDto {
    @NotBlank(message = "수상명은 필수입니다.")
    private String awardName;
    private String organization;
    private LocalDate awardDate;
}
