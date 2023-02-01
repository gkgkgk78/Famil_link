package com.famillink.model.domain.param;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FlaskModelDTO {

    // 가족 계정의 uid
    private Long uid;

    //가족 계정의
    private String model_path;

    public FlaskModelDTO(Long uid, String model_path) {
        this.uid = uid;
        this.model_path = model_path;
    }

    public FlaskModelDTO(Long uid) {
        this.uid = uid;
    }
}

