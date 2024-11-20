package com.example.shoppro.dto;

import com.example.shoppro.constant.Role;
import com.example.shoppro.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter @ToString
@Builder @NoArgsConstructor @AllArgsConstructor
public class MemberDTO {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로")
    private String name;

    @NotBlank
    @Email(message = "이메일 형식으로 작성해주세요")
    @Size(min = 2, max = 20, message = "이메일은 2자 이상 20자 이하로")
    private String email;

    @NotBlank
    @Size(min = 4, max = 10)
    private String password;

    private String address;

    private Role role;

    public Member dtoToEntity (MemberDTO memberDTO){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member member = new Member();
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setAddress(memberDTO.getAddress());

        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        member.setRole(Role.ADMIN);

        return member;
    }
}