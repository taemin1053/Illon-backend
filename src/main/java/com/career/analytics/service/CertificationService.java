package com.career.analytics.service;

import com.career.analytics.dto.CertificationRequestDto;
import com.career.analytics.dto.CertificationResponseDto;
import com.career.analytics.entity.Certification;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.CertificationNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.CertificationRepository;
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
public class CertificationService {
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;

    //자격증 조회
    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getMyCertifications() {
        Long userId = getCurrentUserId();

        return certificationRepository.findByUserIdOrderByIssueDateDesc(userId).stream()
                .map(CertificationResponseDto::new)
                .collect(Collectors.toList());
    }

    //자격증 추가
    @Transactional
    public CertificationResponseDto createCertification(CertificationRequestDto dto){
        Long userId = getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        //빌더 사용
        Certification certification =Certification.builder()
                .user(user)
                .certificationName(dto.getCertificationName())
                .issuingOrganization(dto.getIssuingOrganization())
                .issueDate(dto.getIssueDate())
                .expiryDate(dto.getExpiryDate())
                .build();

        Certification savedCertification = certificationRepository.save(certification);

        return new CertificationResponseDto(savedCertification);
    }

    //자격증 수정
    @Transactional
    public CertificationResponseDto updateCertification(Long certificationId,CertificationRequestDto dto){
        Long userId = getCurrentUserId();

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CertificationNotFoundException(certificationId));

        //본인 확인
        if (!certification.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 자격증만 수정가능합니다.");
        }
        certification.update(
                dto.getCertificationName(),
                dto.getIssuingOrganization(),
                dto.getIssueDate(),
                dto.getExpiryDate()
        );

        //더티 채킹
        return new CertificationResponseDto(certification);
    }

    //자격증 삭제
    @Transactional
    public void deleteCertification(Long certificationId){
        Long userId = getCurrentUserId();

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CertificationNotFoundException(certificationId));

        //본인 확인
        if( !certification.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인 자격증만 삭제 가능");
        }
        certificationRepository.delete(certification);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
