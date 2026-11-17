package com.career.analytics.service;

import com.career.analytics.dto.AwardRequestDto;
import com.career.analytics.dto.AwardResponseDto;
import com.career.analytics.entity.Award;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.AwardNotFoundException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.AwardRepository;
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwardService {

    private final AwardRepository awardRepository;
    // ✨ UserRepository를 ResumeRepository로 교체
    private final ResumeRepository resumeRepository;

    // ✨ 1. 파라미터로 resumeId를 받아 특정 이력서의 수상 내역 조회
    public List<AwardResponseDto> getAwardsByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        // 이력서 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        // ✨ 아까 수정한 레포지토리 메서드 사용
        return awardRepository.findByResumeIdOrderByAwardDateDesc(resumeId).stream()
                .map(AwardResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    // ✨ 2. 파라미터로 resumeId 추가
    public AwardResponseDto createAward(Long resumeId, AwardRequestDto dto) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 수상 정보를 추가할 수 있습니다.");
        }

        // ✨ 3. 빌더 대신 우리가 엔티티에 만들었던 정적 팩토리 메서드(create) 사용
        // DTO에 추가했던 description까지 완벽하게 넘겨줍니다.
        Award award = Award.create(
                resume,
                dto.getAwardName(),
                dto.getOrganization(),
                dto.getAwardDate(),
                dto.getDescription()
        );

        return AwardResponseDto.from(awardRepository.save(award));
    }

    @Transactional
    public AwardResponseDto updateAward(Long awardId, AwardRequestDto dto) {
        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));

        // ✨ 4. 본인 확인 로직 수정: resume -> user 순으로 타고 올라갑니다.
        if (!award.getResume().getUser().getId().equals(getCurrentUserId())) {
            throw new UnauthorizedAccessException("본인의 수상 정보만 수정할 수 있습니다.");
        }

        // ✨ 5. 엔티티 비즈니스 로직(update) 호출 시 description 파라미터 추가
        award.update(
                dto.getAwardName(),
                dto.getOrganization(),
                dto.getAwardDate(),
                dto.getDescription()
        );

        return AwardResponseDto.from(award);
    }

    @Transactional
    public void deleteAward(Long awardId) {
        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));

        // ✨ 6. 본인 확인 로직 수정
        if (!award.getResume().getUser().getId().equals(getCurrentUserId())) {
            throw new UnauthorizedAccessException("본인의 수상 정보만 삭제할 수 있습니다.");
        }

        awardRepository.delete(award);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)){
            throw new UnauthorizedAccessException("인증 정보가 유효하지 않습니다.");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}