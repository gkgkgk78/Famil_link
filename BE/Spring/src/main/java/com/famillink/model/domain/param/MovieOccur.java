package com.famillink.model.domain.param;

import lombok.Data;


@Data
public class MovieOccur {
    // 보내는 사람
    private Long member_to;

    // 받는 사람
    private Integer absent;

    private String recently_date;


}
