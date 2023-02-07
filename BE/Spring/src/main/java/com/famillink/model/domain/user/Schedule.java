package com.famillink.model.domain.user;

import com.famillink.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {


    @ApiModelProperty(hidden = true)
    private long uid; //일정 uid

    private long account_uid; //가족 uid

    private long member_uid; //등록자 uid

    @NotNull(groups = {ValidationGroups.regist.class}, message = "일정은 공백일 수 없습니다")
    private String content; //일정 내용

    @NotNull(groups = {ValidationGroups.date.class}, message = "날짜는 공백일 수 없습니다")
    private Date date; //(일단은 시간 없는) 날짜


}
