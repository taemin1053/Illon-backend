package com.career.analytics.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    // 🔥 추가: 프론트엔드에서 넘겨주는 포트원 본인인증 성공 ID
    @NotBlank(message = "본인인증은 필수입니다.")
    private String verificationId;

    /* 이름, 성별, 생년월일, 전화번호 등은
       프론트에서 받지 않고 verificationId를 통해
       백엔드에서 포트원 API로 직접 조회하여 저장합니다.
    */
}