package com.career.analytics.service;

import com.career.analytics.dto.LanguageRequestDto;
import com.career.analytics.dto.LanguageResponseDto;
import com.career.analytics.entity.Language;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.LanguageNotFoundException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.LanguageRepository;
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
public class LanguageService {
    private final LanguageRepository languageRepository;

    // ✨ 1. UserRepository 대신 ResumeRepository를 주입받습니다.
    private final ResumeRepository resumeRepository;

    // 어학 정보 조회 (이력서 기준)
    public List<LanguageResponseDto> getLanguagesByResume(Long resumeId) {
        Long userId = getCurrentUserId();

        // 이력서 존재 여부 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        return languageRepository.findByResumeIdOrderByTestDateDesc(resumeId).stream()
                .map(LanguageResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public LanguageResponseDto createLanguage(Long resumeId, LanguageRequestDto dto) {
        Long userId = getCurrentUserId();

        // ✨ 2. 이력서 소유권 검증 로직 추가
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 어학 정보를 추가할 수 있습니다.");
        }

        // ✨ 3. 빌더 대신 엔티티에 만든 정적 팩토리 메서드(create) 사용 (description 포함)
        Language language = Language.create(
                resume,
                dto.getLanguageName(),
                dto.getTestName(),
                dto.getScore(),
                dto.getTestDate(),
                dto.getDescription()
        );

        return LanguageResponseDto.from(languageRepository.save(language));
    }

    @Transactional
    public LanguageResponseDto updateLanguage(Long languageId, LanguageRequestDto dto) {
        Long userId = getCurrentUserId();
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        // ✨ 4. 본인 확인 로직 수정: language -> resume -> user
        if (!language.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 어학 정보만 수정할 수 있습니다.");
        }

        // ✨ update 메서드 호출 시 description 추가
        language.update(
                dto.getLanguageName(),
                dto.getTestName(),
                dto.getScore(),
                dto.getTestDate(),
                dto.getDescription()
        );
        return LanguageResponseDto.from(language);
    }

    @Transactional
    public void deleteLanguage(Long languageId) {
        Long userId = getCurrentUserId();
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        if (!language.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 어학 정보만 삭제할 수 있습니다.");
        }
        languageRepository.delete(language);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new UnauthorizedAccessException("인증 정보가 유효하지 않습니다.");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}