package com.famillink.model.domain.user;

import lombok.Data;

@Data
public class Member_Login {


    protected String name;

    protected String nickname;

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public Member_Login(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }
}
