package com.career.analytics.security;

import com.career.analytics.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
// ✨ 핵심: UserDetails와 OAuth2User를 동시에 상속받아 하이브리드 객체로 만듭니다.
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // ✨ 소셜 로그인에서 넘어온 유저 정보(JSON 파싱 결과)를 담을 맵
    private Map<String, Object> attributes;

    // 1. 일반 로그인용 기본 생성자
    public UserDetailsImpl(Long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // ✨ 2. CustomOAuth2UserService에서 사용할 소셜 로그인용 생성자 추가
    public UserDetailsImpl(Users user, Map<String, Object> attributes) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        //  포인트: "ROLE_" 띄어쓰기/언더바가 없으면 나중에 시큐리티 필터가 권한을 인식 못 할 수 있습니다. 언더바를 추가했습니다.
        this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        this.attributes = attributes;
    }

    public static UserDetailsImpl build(Users user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name()); // ⚠️ 여기도 수정 완료

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }

    // ==========================================
    // OAuth2User 인터페이스 오버라이드 메서드 (소셜 로그인용)
    // ==========================================
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // 소셜 로그인 시 고유 식별자로 사용할 값을 반환합니다. 보통 email이나 DB PK(id)를 씁니다.
        return email;
    }

    // ==========================================
    // UserDetails 인터페이스 오버라이드 메서드 (일반 로그인용)
    // ==========================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}