package com.career.analytics.dto;

import com.career.analytics.entity.CareerLevel;
import com.career.analytics.entity.Gender;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileUpdateRequestDto {

    @Size(min= 2, max =5)
    private String name;

    private Gender gender;

    private LocalDate birthDate;

    @Size(max=1000)
    private String bio;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 하이픈(-) 없이 숫자만 10~11자리로 입력해주세요.")
    private String phone;
    private String profileImageUrl;

    private CareerLevel careerLevel;

}
