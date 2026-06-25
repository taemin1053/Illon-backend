package com.career.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.presigned.expiration-minutes}")
    private int expirationMinutes;

    /**
     * S3 Object Key를 바탕으로 사전 설정된 유효 기간 동안 유효한 GET 요청용 Presigned URL을 생성합니다.
     *
     * @param key S3 객체의 Key (예: resumes/uuid.pdf)
     * @return 임시 다운로드용 Presigned URL 문자열
     */
    public String generatePresignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        String presignedUrl = presignedGetObjectRequest.url().toString();
        log.debug("Presigned URL 생성 완료 (Key: {}, 유효 시간: {}분): {}", key, expirationMinutes, presignedUrl);
        return presignedUrl;
    }

    /**
     * 전체 S3 객체 URL에서 Object Key만 추출합니다.
     * 예: https://illo-on-resume.s3.ap-northeast-2.amazonaws.com/resumes/uuid.pdf -> resumes/uuid.pdf
     *
     * @param url DB에 저장되어 있는 전체 S3 객체 URL
     * @return 파싱된 S3 Object Key
     */
    public String extractKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // "/resumes/uuid.pdf"
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (Exception e) {
            log.warn("URL 파싱 실패, 문자열 조작으로 Key 추출 시도: {}", url);
            int index = url.indexOf(".com/");
            if (index != -1) {
                return url.substring(index + 5);
            }
            return url;
        }
    }
}
