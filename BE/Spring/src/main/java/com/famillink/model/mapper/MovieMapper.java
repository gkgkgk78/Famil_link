package com.famillink.model.mapper;

import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MovieMapper {
    void sendMovie(@Param(value = "sender") MovieSenderDTO sender, @Param("path") String path);

    String getMoviePath(Long movie_uid);


    String family_validation(MovieSenderDTO sender);


    MovieSenderDTO getMovie(Long movie_uid);

    void setMovie(Long movie_uid);

    List<MovieSenderDTO> findMovieByMemberTo(Long to_member_uid);


}
