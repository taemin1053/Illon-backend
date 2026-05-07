package com.career.analytics.service;

import com.career.analytics.dto.PortfolioRequestDto;
import com.career.analytics.dto.PortfolioResponseDto;
import com.career.analytics.entity.Portfolio;
import com.career.analytics.entity.Resume;
import com.career.analytics.exception.PortfolioNotFoundException;
import com.career.analytics.exception.ResumeNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.repository.PortfolioRepository;
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
@Transactional
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    // ✨ 1. UserRepository 퇴출, ResumeRepository 등판!
    private final ResumeRepository resumeRepository;

    @Transactional
    public PortfolioResponseDto createPortfolio(Long resumeId, PortfolioRequestDto dto) { // ✨ 파라미터 변경
        Long userId = getCurrentUserId();

        // ✨ 이력서 존재 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서에만 포트폴리오를 추가할 수 있습니다.");
        }

        // ✨ 2. 정적 팩토리 메서드(create) 사용
        Portfolio portfolio = Portfolio.create(
                resume,
                dto.getPortfolioName(),
                dto.getUrl()
        );
        return PortfolioResponseDto.from(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponseDto> getPortfoliosByResume(Long resumeId) { // ✨ 메서드명 및 파라미터 변경
        Long userId = getCurrentUserId();

        // ✨ 이력서 존재 및 소유권 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("본인의 이력서 정보만 조회할 수 있습니다.");
        }

        // ✨ 3. 아까 Repository에 만들어둔 메서드 사용
        return portfolioRepository.findByResumeIdOrderByCreatedAtDesc(resumeId).stream()
                .map(PortfolioResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioResponseDto updatePortfolio(Long id, PortfolioRequestDto dto) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException(id));

        // ✨ 4. 본인 확인 로직 변경: portfolio -> resume -> user
        if(!portfolio.getResume().getUser().getId().equals(getCurrentUserId())){
            throw new UnauthorizedAccessException("자신의 포트폴리오만 수정가능합니다.");
        }

        // ✨ 5. 메서드명 변경 (updatePortfolio -> update)
        portfolio.update(
                dto.getPortfolioName(),
                dto.getUrl()
        );

        return PortfolioResponseDto.from(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException(id));

        // ✨ 본인 확인 로직 변경
        if(!portfolio.getResume().getUser().getId().equals(getCurrentUserId())){
            throw new UnauthorizedAccessException("자신의 포트폴리오만 삭제가능합니다.");
        }

        portfolioRepository.delete(portfolio);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)){
            throw new UnauthorizedAccessException("인증 정보가 유효하지 않습니다.");
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}