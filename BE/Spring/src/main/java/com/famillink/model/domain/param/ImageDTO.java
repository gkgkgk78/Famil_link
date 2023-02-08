package com.famillink.model.domain.param;

import com.famillink.annotation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    @NotNull(groups = {ValidationGroups.member_login.class}, message = "반드시 입력해 주세요")
    private Long uid; //


    @NotNull(groups = {ValidationGroups.member_login.class}, message = "반드시 입력해 주세요")
    private List<List<List<Integer>>> json;


}
