package com.famillink.model.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    private long uid; //일정 uid

    private long accountUid; //가족 uid

    private long memberUid; //등록자 uid

    private String context; //일정 내용

    private Date date; //일단은 시간 없이 날짜만 설정


}
