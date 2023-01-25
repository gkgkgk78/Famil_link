package com.famillink.model.domain.user;

import com.famillink.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.util.Collection;

@Data
public class Member implements UserDetails {
    @NotNull
    @ApiModelProperty(hidden = true)
    protected Long uid;


    protected String user_uid;

//    @NotNull(groups = {ValidationGroups.signup.class, ValidationGroups.login.class}, message = "비밀번호는 공백일 수 없습니다.")
//    @Size(min = 8, max = 30, groups = {ValidationGroups.signup.class}, message = "비밀번호는은 8글자 이상 30글자 이하입니다.")
//    protected String pw;

    @NotNull(groups = {ValidationGroups.signup.class, ValidationGroups.find_password.class}, message = "이름은 공백일 수 없습니다.")
    @Size(min = 2, max = 20, groups = {ValidationGroups.signup.class, ValidationGroups.find_password.class}, message = "이름은 2글자이상 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", groups = {ValidationGroups.signup.class, ValidationGroups.find_password.class}, message = "이름은 특수문자와 초성은 사용불가능합니다")
    protected String name;


    protected String sdate;

    protected String model_path;



    @ApiModelProperty(hidden = true)
    protected String role;

    @ApiModelProperty(hidden = true)
    protected String refresh_token;

    @ApiModelProperty(hidden = true)
    protected String salt;

    @ApiModelProperty(hidden = true)
    protected Short level;

    protected String pw;

    // 이하 코드는 security 를 위한 용도
    @ApiModelProperty(hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.model_path;//우선 이렇게 해둠... 어떻게 넘기면 좋을지 궁금해서 이렇게 함
    }

    @Override
    public String getUsername() {
        return Long.toString(this.uid);
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
