package com.career.analytics.service;

import com.career.analytics.dto.CertificationRequestDto;
import com.career.analytics.dto.CertificationResponseDto;
import com.career.analytics.entity.Certification;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.CertificationNotFoundException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.CertificationRepository;
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
public class CertificationService {

    private final CertificationRepository certificationRepository;
    // ✨ 1. UserRepository 퇴출, ResumeRepository 등판!
    private final ResumeRepository resumeRepository;

    // 자격증 조회
    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getCertificationsByResume(Long resumeId) { // ✨ 파라미터 변경
        Long userId = getCurrentUserId();

        // ✨ 이력서 존재 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        return certificationRepository.findByResumeIdOrderByIssueDateDesc(resumeId).stream() // ✨ 최신 Repository 메서드 사용
                .map(CertificationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 자격증 추가
    @Transactional
    public CertificationResponseDto createCertification(Long resumeId, CertificationRequestDto dto){ // ✨ 파라미터 변경
        Long userId = getCurrentUserId();

        // ✨ 이력서 조회 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 자격증을 추가할 수 있습니다.");
        }

        // ✨ 2. 날것의 Builder를 버리고, 우리가 만든 완벽한 정적 팩토리 메서드(create) 사용!
        Certification certification = Certification.create(
                resume,
                dto.getCertificationName(),
                dto.getIssuingOrganization(),
                dto.getIssueDate(),
                dto.getExpiryDate(),
                // ✨ 3. 방금 뚫어놓은 상세 내용(description) 완벽하게 꽂아 넣기!
                dto.getDescription()
        );

        Certification savedCertification = certificationRepository.save(certification);

        return new CertificationResponseDto(savedCertification);
    }

    // 자격증 수정
    @Transactional
    public CertificationResponseDto updateCertification(Long certificationId, CertificationRequestDto dto){
        Long userId = getCurrentUserId();

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CertificationNotFoundException(certificationId));

        // ✨ 4. 본인 확인 로직 변경: certification -> resume -> user 순으로 타고 올라감
        if (!certification.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 자격증만 수정가능합니다.");
        }

        // ✨ update 메서드에 description 파라미터 추가!
        certification.update(
                dto.getCertificationName(),
                dto.getIssuingOrganization(),
                dto.getIssueDate(),
                dto.getExpiryDate(),
                dto.getDescription()
        );

        // 더티 체킹
        return new CertificationResponseDto(certification);
    }

    // 자격증 삭제
    @Transactional
    public void deleteCertification(Long certificationId){
        Long userId = getCurrentUserId();

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CertificationNotFoundException(certificationId));

        // ✨ 본인 확인 로직 변경
        if(!certification.getResume().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인 자격증만 삭제 가능합니다.");
        }

        certificationRepository.delete(certification);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}