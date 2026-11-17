package com.career.analytics.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class JobSearchCondition {
    @Getter
    private String keyword;
    @Getter
    private String location;
    @Getter
    private String career;
    @Getter
    private String jobCategory;
    @Getter
    private String education;
    private Integer page;
    private Integer size;

    // 💡 Getter 재정의를 통한 무조건적인 기본값 보장 및 음수 방어
    public Integer getPage() {
        return (page == null || page < 1) ? 1 : page;
    }

    public Integer getSize() {
        return (size == null || size < 1) ? 20 : size;
    }

    /**
     * Caffeine 캐시 키 생성을 위한 고유 문자열 변환 메서드
     */
    public String toCacheKey() {
        return String.format("%s_%s_%s_%s_%s_%d_%d",
                keyword != null ? keyword : "",
                location != null ? location : "",
                career != null ? career : "",
                jobCategory != null ? jobCategory : "",
                education != null ? education : "",
                getPage(),  // 보장된 page 기본값 사용
                getSize()   // 보장된 size 기본값 사용
        );
    }
}
