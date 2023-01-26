package com.famillink.model.domain.user;

import com.famillink.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    protected String nickname;

    @ApiModelProperty(hidden = true)
    protected String role;

    @ApiModelProperty(hidden = true)
    protected String refresh_token;

    @ApiModelProperty(hidden = true)
    protected String salt;

    @ApiModelProperty(hidden = true)
    protected Short level;

    @ApiModelProperty(hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.pw;
    }

    @Override
    public String getUsername() {
        return this.email;
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

    public Account(String email, String password){
        this.pw = password;
        this.email = email;
    }

}
