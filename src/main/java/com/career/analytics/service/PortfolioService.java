package com.career.analytics.service;

import com.career.analytics.dto.PortfolioRequestDto;
import com.career.analytics.dto.PortfolioResponseDto;
import com.career.analytics.entity.Portfolio;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.PortfolioNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.PortfolioRepository;
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    @Transactional
    public PortfolioResponseDto createPortfolio(PortfolioRequestDto dto) {
        Long userId = getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .portfolioName(dto.getPortfolioName())
                .url(dto.getUrl())
                .build();
        return PortfolioResponseDto.from(portfolioRepository.save(portfolio));
    }
    @Transactional
    public List<PortfolioResponseDto> getMyPortfolios() {
        Long userId = getCurrentUserId();
        return portfolioRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PortfolioResponseDto::from) //.map(portfolio -> PortfolioResponseDto.from(portfolio))
                .collect(Collectors.toList());
    }
    @Transactional
    public PortfolioResponseDto updatePortfolio(Long id, PortfolioRequestDto dto) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));

        if(!portfolio.getUser().getId().equals(getCurrentUserId())){
            throw new UnauthorizedAccessException("자신의 포트폴리오만 수정가능");
        }

        portfolio.updatePortfolio(
                dto.getPortfolioName(),
                dto.getUrl()
        );
        return PortfolioResponseDto.from(portfolioRepository.save(portfolio));
    }

    public  void  deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));

        if(!portfolio.getUser().getId().equals(getCurrentUserId())){
            throw new UnauthorizedAccessException("자신의 포트폴리오만 삭제가능");
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
