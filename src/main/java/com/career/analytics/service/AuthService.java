package com.career.analytics.service;

import com.career.analytics.dto.LoginRequestDto;
import com.career.analytics.dto.SignupRequestDto;
import com.career.analytics.dto.TokenResponseDto;
import com.career.analytics.entity.Profile;
import com.career.analytics.entity.Role;
import com.career.analytics.entity.Users;
import com.career.analytics.exception.DuplicateEmailException;
import com.career.analytics.exception.UserNotFoundException;
import com.career.analytics.repository.ProfileRepository;
import com.career.analytics.repository.UserRepository;
import com.career.analytics.security.JwtTokenProvider;
import com.career.analytics.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //생성자 주입 자동화
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public TokenResponseDto signup(SignupRequestDto signupDto) {
        //이메일 중복 체크
        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.: " +signupDto.getEmail());
        }

        //User 엔티티 생성
        Users user = Users.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .role(Role.USER)
                .build();
        Users savedUser = userRepository.save(user);

        //Profile 엔티티 생성
        Profile profile = Profile.createProfile(savedUser, signupDto);
        profileRepository.save(profile);

        //자동 로그인 jwt 토큰 발급
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupDto.getEmail(), signupDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new TokenResponseDto(
                accessToken,
                refreshToken,
                savedUser.getId(),
                savedUser.getEmail(),
                signupDto.getName(),
                savedUser.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        // getContext() 사용
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //커스텀 예외 발생
        Users user = userRepository.findById(userDetails.getId())
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userDetails.getId()));

        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        String name = profile != null ? profile.getName() : null;

        return new TokenResponseDto(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                name,
                user.getRole().name()
        );
    }
}