package com.career.analytics.service;

import com.career.analytics.dto.CoverLetterRequestDto;
import com.career.analytics.dto.CoverLetterResponseDto;
import com.career.analytics.entity.CoverLetter;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.CoverLetterException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.CoverLetterRepository;
import com.career.analytics.repository.ResumeRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    // ✨ 1. UserRepository 날리고 ResumeRepository 등판!
    private final ResumeRepository resumeRepository;

    /**
     * 자기소개서 작성 (1:1 관계 방어 로직 포함)
     */
    @Transactional
    public CoverLetterResponseDto createCoverLetter(Long resumeId, CoverLetterRequestDto requestDto) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 자기소개서를 작성할 수 있습니다.");
        }

        // ✨ 2. 핵심 방어 로직: 1:1 관계이므로 이미 자소서가 있다면 예외 발생!
        if (coverLetterRepository.findByResumeId(resumeId).isPresent()) {
            throw new IllegalStateException("이 이력서에는 이미 자기소개서가 존재합니다. 수정을 이용해주세요.");
        }

        // ✨ 3. 빌더 대신 정적 팩토리 메서드(create) 사용
        CoverLetter coverLetter = CoverLetter.create(resume, requestDto.getContent());

        return CoverLetterResponseDto.from(coverLetterRepository.save(coverLetter));
    }

    /**
     * 특정 이력서의 자기소개서 단건 조회 (List가 아닙니다!)
     */
    public CoverLetterResponseDto getCoverLetterByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("조회 권한이 없습니다.");
        }

        CoverLetter coverLetter = coverLetterRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalStateException("등록된 자기소개서가 없습니다."));

        return CoverLetterResponseDto.from(coverLetter);
    }

    /**
     * 자기소개서 수정
     */
    @Transactional
    public CoverLetterResponseDto updateCoverLetter(Long coverLetterId, CoverLetterRequestDto requestDto) {
        Long userId = getCurrentUserId();
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterException(coverLetterId));

        // ✨ 4. 작성자 본인 확인 로직 수정 (coverLetter -> resume -> user)
        if (!coverLetter.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("수정 권한이 없습니다.");
        }

        coverLetter.update(requestDto.getContent());

        return CoverLetterResponseDto.from(coverLetter);
    }

    /**
     * 자기소개서 삭제
     */
    @Transactional
    public void deleteCoverLetter(Long coverLetterId) {
        Long userId = getCurrentUserId();
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterException(coverLetterId));

        // ✨ 작성자 본인 확인 로직 수정
        if (!coverLetter.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("삭제 권한이 없습니다.");
        }

        coverLetterRepository.delete(coverLetter);
    }

    // ✨ 공통 사용자 인증 메서드 추가
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)){
            throw new UnauthorizedAccessException("인증 정보가 유효하지 않습니다.");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}