package com.famillink.model.domain.user;

import com.famillink.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.util.Collection;

@Data
public class Account implements UserDetails {

    @NotNull
    @ApiModelProperty(hidden = true)
    protected Long uid;

    @NotNull(groups = {ValidationGroups.signup.class, ValidationGroups.signup.class, ValidationGroups.find_password.class}, message = "이메일은 공백일 수 없습니다.")
    @Email(groups = {ValidationGroups.signup.class}, message = "이메일 형식이 아닙니다.")
    protected String email;

    @NotNull(groups = {ValidationGroups.signup.class, ValidationGroups.login.class}, message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 30, groups = {ValidationGroups.signup.class}, message = "비밀번호는은 8글자 이상 30글자 이하입니다.")
    protected String pw;

    @NotNull(groups = {ValidationGroups.signup.class}, message = "집주소는 공백일 수 없습니다.")
    protected String address;

    @NotNull(groups = {ValidationGroups.signup.class}, message = "대표자 연락처는 공백일 수 없습니다.")
    @Size(min = 8, max = 11, groups = {ValidationGroups.signup.class})
    protected String phone;

    @NotNull(groups = {ValidationGroups.signup.class}, message = "별명은 공백일 수 없습니다.")
    @Size(min = 2, max = 30, groups = {ValidationGroups.signup.class}, message = "별명은 2글자이상 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", groups = {ValidationGroups.signup.class}, message = "별명은 특수문자와 초성은 사용불가능합니다")
    protected String nickname;

    @ApiModelProperty(hidden = true)
    protected String role;

    @ApiModelProperty(hidden = true)
    protected String refresh_token;

    @ApiModelProperty(hidden = true)
    protected String salt;

    @ApiModelProperty(hidden = true)
    protected Short level;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
