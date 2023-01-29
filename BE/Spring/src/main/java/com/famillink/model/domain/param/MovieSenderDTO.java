package com.famillink.model.domain.param;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MovieSenderDTO {
    // 보내는 사람
    private Long from_member_uid;

    // 받는 사람
    private Long to_member_uid;
}
