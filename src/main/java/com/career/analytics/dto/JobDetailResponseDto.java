package com.career.analytics.dto;

public record JobDetailResponseDto(
        String id,
        String title,
        String company,
        String location,
        String jobType,      // 카멜케이스 변환
        String occupation,
        String careerType,   // 카멜케이스 변환
        String education,
        String salary,
        String description,
        String requirements,
        String preferred,
        String benefits,
        String process,
        String deadline,
        String source,
        String url,
        int viewCount,       // 카멜케이스 변환
        String createdAt,    // 카멜케이스 변환
        String lat,          // 💡 추가: 위도 (Kakao API y값)
        String lng           // 💡 추가: 경도 (Kakao API x값)
) {}