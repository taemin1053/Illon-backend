package com.career.analytics.service;

import com.career.analytics.entity.Users;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyService {

    // 🔥 핵심: DB에서 유저 정보를 가져와야 하므로 UserRepository를 주입받습니다.
    private final UserRepository userRepository;

    @Transactional // 🔥 중요: 트랜잭션이 있어야 데이터베이스에 변경 사항이 저장됩니다.
    public void completeSurvey(Long userId) {
        // 1. 전달받은 userId로 DB에서 유저를 찾습니다.
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. 유저의 상태를 '설문 완료'로 변경합니다. (Users 엔티티에 만든 메서드 호출)
        user.completeSurvey();

        // 3. @Transactional이 붙어있으므로 상태만 바꿔주면 자동으로 DB에 Update 쿼리가 날아갑니다.
    }
}