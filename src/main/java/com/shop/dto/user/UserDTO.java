package com.shop.dto.user;

import com.shop.dto.ImageFileDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements UserDetails, OAuth2User {
    @NotBlank
    @Length(min = 4, max = 15)
    private String id;
    @NotBlank
    @Length(min = 88)
    private String ci;
    @NotBlank(message = "password는 4-8자리여야함!")
    @Length(min = 4, max = 8)
    private String password;
    @Email
    private String email;
    @Pattern(regexp = "[0-9]{3}-[0-9]{3,4}-[0-9]{4}")
    private String tel;
    @Valid
    @NotNull
    private ImageFileDTO imageFile;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;

    private String token;

    private SnsInfoDTO snsInfo;

    private String pwReToken;

    private LocalDateTime pwReTokenExpire;

    @Override
    public Map<String, Object> getAttributes() {
        return this.snsInfo.getAttributes();
    }

    @Override
    public String getName() {
        return this.id;
    }


    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.id;
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