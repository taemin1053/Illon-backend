package com.career.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 카카오 Local 주소 검색 API 응답 DTO
 */
public record KakaoAddressResponseDto(
        @JsonProperty("meta") MetaDto meta,
        @JsonProperty("documents") List<DocumentDto> documents
) {
    public record MetaDto(
            @JsonProperty("total_count") int totalCount,
            @JsonProperty("pageable_count") int pageableCount,
            @JsonProperty("is_end") boolean isEnd
    ) {}

    public record DocumentDto(
            @JsonProperty("address_name") String addressName,
            @JsonProperty("x") String x, // 경도 (Longitude)
            @JsonProperty("y") String y  // 위도 (Latitude)
    ) {}
}
