package com.famillink.model.domain.param;

import lombok.Data;


@Data
public class MovieSenderDTO {
    // 보내는 사람
    private Long from_member_uid;

    // 받는 사람
    private Long to_member_uid;

    public MovieSenderDTO(Long from_member_uid, Long to_member_uid) {
        this.from_member_uid = from_member_uid;
        this.to_member_uid = to_member_uid;
    }
}
