package com.career.analytics.controller;

import com.career.analytics.dto.MyPageResponseDto;
import com.career.analytics.security.UserDetailsImpl;
import com.career.analytics.service.MyPageFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "MyPage API", description = "마이페이지 통합 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageFacade myPageFacade;

    @Operation(
            summary = "마이페이지 데이터 전체 통합 조회",
            description = "로그인 유저의 가공된 프로필 카드 정보와 하단 스크랩 페이징 공고 목록(6개 단위)을 한 번에 결합하여 반환합니다."
    )
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("📢 마이페이지 통합 데이터 조회 요청 - 유저 ID: {}, 페이징 조건: {}", userDetails.getId(), pageable);
        MyPageResponseDto response = myPageFacade.getMyPageData(userDetails.getId(), pageable);
        return ResponseEntity.ok(response);
    }
}
