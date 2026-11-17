package com.career.analytics.service;

import com.career.analytics.exception.InvalidFileException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "jpg", "jpeg", "png", "gif", "bmp");

    /**
     * 파일 목록을 순차적으로 S3에 업로드하고 업로드된 URL 목록을 반환합니다.
     *
     * @param files 업로드할 파일 리스트
     * @return S3에 업로드 완료된 URL 리스트
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> uploadedUrls = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("업로드할 파일이 존재하지 않습니다.");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException("업로드할 파일이 비어있거나 올바르지 않습니다.");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);

            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new InvalidFileException("허용되지 않는 파일 확장자입니다: " + extension + " (허용 형식: PDF, JPG, JPEG, PNG, GIF, BMP)");
            }

            String savedFilename = UUID.randomUUID().toString() + "." + extension;
            String key = "resumes/" + savedFilename;

            try (InputStream inputStream = file.getInputStream()) {
                ObjectMetadata metadata = ObjectMetadata.builder()
                        .contentType(file.getContentType())
                        .build();

                s3Template.upload(bucket, key, inputStream, metadata);

                // 최종 생성된 S3 퍼블릭 객체 URL 구성
                String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
                uploadedUrls.add(fileUrl);
                log.info("S3 파일 업로드 성공: {} -> {}", originalFilename, fileUrl);
            } catch (IOException e) {
                log.error("S3 파일 업로드 중 오류 발생: {}", originalFilename, e);
                throw new InvalidFileException("파일 업로드 처리 중 오류가 발생했습니다: " + originalFilename, e);
            }
        }

        return uploadedUrls;
    }

    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new InvalidFileException("파일명이 비어있습니다.");
        }
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            throw new InvalidFileException("파일 확장자가 누락되었습니다.");
        }
        return filename.substring(lastIndexOf + 1);
    }
}
