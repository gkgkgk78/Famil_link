package com.famillink.model.mapper;

import com.famillink.model.domain.param.FlaskModelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FlaskMapper {
    void sendMovie(@Param(value = "sender") FlaskModelDTO sender, @Param("path") String path);

    String getMoviePath(Long movie_uid);


    String family_validation(FlaskModelDTO sender);


    FlaskModelDTO getMovie(Long movie_uid);

    void setMovie(Long movie_uid);


}
