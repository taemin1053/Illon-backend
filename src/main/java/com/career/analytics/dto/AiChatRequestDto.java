package com.career.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequestDto {

    private String message;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("is_quick_replies")
    private boolean isQuickReplies;

    @JsonProperty("quick_replies_category")
    private String quickRepliesCategory;

    private List<ChatHistoryDto> history;

    @JsonProperty("user_info")
    private UserInfo userInfo;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {

        @JsonProperty("job_type")
        private String jobType;

        @JsonProperty("region")
        private String region;

        @JsonProperty("career_type")
        private String careerType;

        @JsonProperty("occupation")
        private String occupation;

        private String education;

        private String university;

        private String major;

        @JsonProperty("career_years")
        private String careerYears;

        @JsonProperty("company_name")
        private String companyName;
    }
}
