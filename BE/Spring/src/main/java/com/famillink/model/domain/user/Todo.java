package com.famillink.model.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {


    @NotNull
    @ApiModelProperty(hidden = true)
    protected Long uid;//현재 todo의 식별자 id

    @NotNull
    @ApiModelProperty(hidden = true)
    protected Long account_uid;//가족 계정의 id


    String content;//내용

    int status;


    @ApiModelProperty(hidden = true)
    protected String sdate;//쓴날짜

    public Todo(Long account_uid, String content) {
        this.account_uid = account_uid;
        this.content = content;
    }
//`uid`,`content`,`sdate`,`status`
    public Todo(Long uid, String content, String sdate,int status) {
        this.uid = uid;
        this.content = content;
        this.status = status;
        this.sdate = sdate;
    }
}
