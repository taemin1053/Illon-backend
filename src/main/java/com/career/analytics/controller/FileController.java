package com.career.analytics.controller;

import com.career.analytics.service.S3UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Upload API", description = "범용 파일 업로드 API")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final S3UploadService s3UploadService;

    @Operation(summary = "파일 다중 업로드 (Pre-upload)", description = "PDF, JPG, JPEG, PNG, GIF, BMP 형식의 파일을 S3에 업로드하고 객체 URL 목록을 반환합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadFiles(@RequestPart("files") List<MultipartFile> files) {
        List<String> fileUrls = s3UploadService.uploadFiles(files);
        return ResponseEntity.ok(fileUrls);
    }
}
