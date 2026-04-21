package com.career.analytics.service;

import com.career.analytics.dto.LanguageRequestDto;
import com.career.analytics.dto.LanguageResponseDto;
import com.career.analytics.entity.Language;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.LanguageNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.LanguageRepository;
import com.career.analytics.repository.UserRepository;
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
    private final UserRepository userRepository;

    public List<LanguageResponseDto> getMyLanguages() {
        Long userId = getCurrentUserId();

        return languageRepository.findByUserIdOrderByTestDateDesc(userId).stream()
                .map(LanguageResponseDto::from)
                .collect(Collectors.toList());
    }
    @Transactional
    public LanguageResponseDto createLanguage(LanguageRequestDto dto) {
        Long userId = getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Language language = Language.builder()
                .user(user)
                .languageName(dto.getLanguageName())
                .testName(dto.getTestName())
                .score(dto.getScore())
                .testDate(dto.getTestDate())
                .build();
        return LanguageResponseDto.from(languageRepository.save(language));
    }
    @Transactional
    public LanguageResponseDto updateLanguage(Long languageId,LanguageRequestDto dto) {
        Long userId = getCurrentUserId();
        Language language = languageRepository.findById(languageId).orElseThrow(() -> new LanguageNotFoundException(languageId));

        if(!language.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 어학 정보만 수정할 수 있습니다.");
        }
        language.update(
                dto.getLanguageName(), dto.getTestName(), dto.getScore(), dto.getTestDate()
        );
        return  LanguageResponseDto.from(language);
    }
    @Transactional
    public void deleteLanguage(Long languageId) {
        Long userId = getCurrentUserId();
        Language language = languageRepository.findById(languageId).orElseThrow(() -> new LanguageNotFoundException(languageId));
        if(!language.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 어학 정보만 삭제할 수 있습니다.");
        }
        languageRepository.delete(language);
    }
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() ||!(authentication.getPrincipal() instanceof UserDetailsImpl)){
            throw new UnauthorizedAccessException("인증 정보가 유효하지 않습니다.");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}
