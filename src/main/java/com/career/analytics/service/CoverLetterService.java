package com.career.analytics.service;

import com.career.analytics.dto.CoverLetterRequestDto;
import com.career.analytics.dto.CoverLetterResponseDto;
import com.career.analytics.entity.CoverLetter;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.CoverLetterException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.CoverLetterRepository;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final UserRepository userRepository;

    /**
     * 자기소개서 작성
     */
    @Transactional
    public CoverLetterResponseDto createCoverLetter(Long userId, CoverLetterRequestDto requestDto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        CoverLetter coverLetter = CoverLetter.builder()
                .user(user)
                .content(requestDto.getContent())
                .build();

        return CoverLetterResponseDto.from(coverLetterRepository.save(coverLetter));
    }

    /**
     * 특정 자기소개서 조회
     */
    public CoverLetterResponseDto getCoverLetter(Long userId, Long coverLetterId) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterException(coverLetterId));

        // 👉 IDOR 방어 로직: 내 자소서가 아니면 예외 발생!
        if (!coverLetter.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("조회 권한이 없습니다.");
        }

        return CoverLetterResponseDto.from(coverLetter);
    }

    public List<CoverLetterResponseDto> getMyCoverLetters(Long userId) {
        return coverLetterRepository.findAllByUserId(userId).stream()
                .map(CoverLetterResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 자기소개서 수정
     */
    @Transactional
    public CoverLetterResponseDto updateCoverLetter(Long userId, Long coverLetterId, CoverLetterRequestDto requestDto) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterException(coverLetterId));
        // 작성자 본인 확인 방어 로직 (보안 필수)
        if (!coverLetter.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("수정 권한이 없습니다.");
        }

        coverLetter.update(requestDto.getContent());

        return CoverLetterResponseDto.from(coverLetter);
    }

    /**
     * 자기소개서 삭제
     */
    @Transactional
    public void deleteCoverLetter(Long userId, Long coverLetterId) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new CoverLetterException(coverLetterId));

        // 작성자 본인 확인 방어 로직 (보안 필수)
        if (!coverLetter.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("삭제 권한이 없습니다.");
        }

        coverLetterRepository.delete(coverLetter);
    }
}