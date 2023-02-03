package com.famillink.model.domain.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    private Long uid;
    private List<List<List<Integer>>> json;


}
