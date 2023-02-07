package com.famillink.model.domain.param;

import lombok.Data;


@Data
public class PhotoSenderDTO {
    // 등록하고자 하는가족
    private Long from_account_uid;

    // 저장하고자 하는 멤버의 이름을 나타냄
    private String name;



}
