package com.famillink.model.domain.param;

import lombok.Data;


@Data
public class MovieOccur {
    // 보내는 사람
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public String getSdate() {
        return sdate;
    }

    // 받는 사람
    private String sdate;

    public MovieOccur(Long uid, String sdate) {
        this.uid = uid;
        this.sdate = sdate;
    }
}
