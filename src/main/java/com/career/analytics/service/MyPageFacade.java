package com.career.analytics.service;

import com.career.analytics.dto.MyPageResponseDto;
import com.career.analytics.dto.MyPageResponseDto.UserProfileDto;
import com.career.analytics.dto.ScrapedJobResponseDto;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyPageFacade {

    private final UserRepository userRepository;
    private final JobBffService jobBffService;

    /**
     * 💡 마이페이지 통합 데이터 조회
     * 외부 API(BFF) 호출 시점에는 DB 커넥션 풀을 쥐고 대기하는 현상을 막기 위해 @Transactional을 명시적으로 제거합니다.
     */
    public MyPageResponseDto getMyPageData(Long userId, Pageable pageable) {
        
        // 1. 단일 쿼리로 유저 정보 및 프로필 패치조인 조회 
        // (Spring Data JPA의 기본 트랜잭션이 작동 후 커넥션 즉시 반납)
        Users user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. 가공 프로필 DTO 생성 (메모리 내 객체 매핑)
        UserProfileDto profileDto = UserProfileDto.from(user);

        // 3. 캐싱된 스크랩 목록 조회 위임 (외부 API 통신 시점에는 트랜잭션이 활성화되지 않음)
        Page<ScrapedJobResponseDto> scrapedJobs = jobBffService.getScrapedJobs(userId, pageable);

        return new MyPageResponseDto(profileDto, scrapedJobs);
    }
}
