package com.famillink.model.mapper;

import com.famillink.model.domain.param.MovieSenderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    void sendMovie(@Param(value = "sender") MovieSenderDTO sender, @Param("path") String path);

    String getMoviePath(Long movie_uid);


    MovieSenderDTO getMovie(Long movie_uid);

    void setMovie(Long movie_uid);

    List<MovieSenderDTO> findMovieByMemberTo(Long to_member_uid);

    int getOneMovie(Long movie_uid) throws Exception;

}
