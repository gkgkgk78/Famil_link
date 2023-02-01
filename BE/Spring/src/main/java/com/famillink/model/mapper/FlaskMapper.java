package com.famillink.model.mapper;

import com.famillink.model.domain.user.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FlaskMapper {
    void sendMovie(@Param(value = "sender") Account sender, @Param("path") String path);

    String getMoviePath(Long movie_uid);


    String family_validation(Account sender);


    Account getMovie(Long movie_uid);

    void setMovie(Long movie_uid);


}
