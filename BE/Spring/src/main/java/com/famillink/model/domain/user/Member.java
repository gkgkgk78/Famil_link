package com.famillink.model.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.util.Collection;

@Data
public class Member implements UserDetails {


    @NotNull
//    @ApiModelProperty(hidden = true)
    protected Long uid;

    protected Long user_uid;

    public void setUser_uid(Long user_uid) {
        this.user_uid = user_uid;
    }

    protected String name;

    @ApiModelProperty(hidden = true)
    protected String sdate;

    protected String nickname;


    @ApiModelProperty(hidden = true)
    protected String role;

    @ApiModelProperty(hidden = true)
    protected String refresh_token;

    @ApiModelProperty(hidden = true)
    protected String salt;

    @ApiModelProperty(hidden = true)
    protected Short level;


    // 이하 코드는 security 를 위한 용도
    @ApiModelProperty(hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return Long.toString(this.uid);//우선 이렇게 해둠... 어떻게 넘기면 좋을지 궁금해서 이렇게 함
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


    //입력 받아야 하는 정보는, name,model_path,nickname


    public Member(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public Member(Long uid, Long user_uid, String name, String nickname) {
        this.uid = uid;
        this.user_uid = user_uid;
        this.name = name;
        this.nickname = nickname;
    }

    public Member(Long uid) {
        this.uid = uid;
    }

    public Member() {


    }
}
