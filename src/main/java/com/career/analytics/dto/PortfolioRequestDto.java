package com.career.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioRequestDto {
    @NotBlank(message = "링크명은 필수입니다.")
    private String portfolioName;
    @NotBlank(message = "URL은 필수입니다.")
    @Pattern(
            regexp = "^https?://.*$",
            message = "URL은 http:// 또는 https://로 시작해야합니다." //통신규약을 지키는 거기 때문에
    )
    private String url;

}
