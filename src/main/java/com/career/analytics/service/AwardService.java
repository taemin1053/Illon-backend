package com.career.analytics.service;

import com.career.analytics.dto.AwardRequestDto;
import com.career.analytics.dto.AwardResponseDto;
import com.career.analytics.entity.Award;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.AwardNotFoundException;
import com.career.analytics.exception.UnauthorizedAccessException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.AwardRepository;
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
public class AwardService {

    private final AwardRepository awardRepository;
    private final UserRepository userRepository;

    public List<AwardResponseDto> getMyAwards() {
        Long userId = getCurrentUserId();
        return awardRepository.findByUserIdOrderByAwardDateDesc(userId).stream()
                .map(AwardResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AwardResponseDto createAward(AwardRequestDto dto) {
        Long userId = getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Award award = Award.builder()
                .user(user)
                .awardName(dto.getAwardName())
                .organization(dto.getOrganization())
                .awardDate(dto.getAwardDate())
                .build();

        return AwardResponseDto.from(awardRepository.save(award));
    }
    @Transactional
    public AwardResponseDto updateAward(Long awardId, AwardRequestDto dto) {
        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));

        // 본인 확인 로직
        if (!award.getUser().getId().equals(getCurrentUserId())) {
            throw new UnauthorizedAccessException("본인의 수상 정보만 수정할 수 있습니다.");
        }


        award.update(
                dto.getAwardName(),
                dto.getOrganization(),
                dto.getAwardDate()
        );

        return AwardResponseDto.from(award);
    }


    @Transactional
    public void deleteAward(Long awardId) {
        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));

        if (!award.getUser().getId().equals(getCurrentUserId())) {
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
