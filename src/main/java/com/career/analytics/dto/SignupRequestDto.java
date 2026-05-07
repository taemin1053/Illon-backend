package com.career.analytics.dto;

import com.career.analytics.entity.CareerLevel;
import com.career.analytics.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    /*
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 5)
    private String name;
    //profile의 name으로 저장된다.

    private Gender gender;

    private LocalDate birthDay;

    @Size(max = 1000, message = "자기소개를 1000자 이내로 작성해주세요")
    private String bio;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 하이픈(-) 없이 숫자만 10~11자리로 입력해주세요.")
    private String phone;

    private String profileImageUrl;
    @NotNull(message = "경력 수준(신입/경력)을 선택해주세요.")
    private CareerLevel careerLevel;*/

}
