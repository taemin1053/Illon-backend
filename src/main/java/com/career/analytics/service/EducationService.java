package com.career.analytics.service;

// 🚨 아까 분리하신 resume 패키지에 맞게 Import 경로 수정 완료!
import com.career.analytics.dto.EducationRequestDto;
import com.career.analytics.dto.EducationResponseDto;
import com.career.analytics.entity.Education;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.EducationNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.EducationRepository;
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
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    // 1. 반환 타입 오류 수정 (Repository -> ResponseDto)
    @Transactional(readOnly = true)
    public List<EducationResponseDto> getMyEducations() {
        Long userId = getCurrentUserId();

        return educationRepository.findByUserId(userId).stream()
                .map(EducationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 2. 반환 타입 오류 수정 (RequestDto -> ResponseDto)
    @Transactional
    public EducationResponseDto createEducation(EducationRequestDto educationRequestDto) {
        Long userId = getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Education education = Education.createEducation(user, educationRequestDto);
        Education savedEducation = educationRepository.save(education);

        return new EducationResponseDto(savedEducation);
    }

    // 학력 수정
    @Transactional
    public EducationResponseDto updateEducation(Long educationId, EducationRequestDto educationDto) {
        Long userId = getCurrentUserId();

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException(educationId.toString()));

        // 본인 확인
        if (!education.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 학력만 수정할 수 있습니다.");
        }

        //엔티티 비즈니스 로직 활용!
        education.update(educationDto);

        // 더티 체킹! save 불필요!
        return new EducationResponseDto(education);
    }

    // 학력 삭제
    @Transactional
    public void deleteEducation(Long educationId) {
        Long userId = getCurrentUserId();

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException(educationId.toString()));

        // 본인 확인
        if (!education.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 학력만 삭제할 수 있습니다.");
        }

        educationRepository.delete(education);
    }

    // 현재 로그인한 사용자 ID 가져오기
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}