package com.career.analytics.dto;

import java.util.List;

public record JobListResponseDto(
        List<JobSummaryResponseDto> jobs
) {}