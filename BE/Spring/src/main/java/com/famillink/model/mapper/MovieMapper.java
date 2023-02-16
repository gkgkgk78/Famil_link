package com.famillink.model.mapper;

import com.famillink.model.domain.param.MovieDTO;
import com.famillink.model.domain.param.MovieOccur;
import com.famillink.model.domain.param.MovieSenderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface MovieMapper {
    void sendMovie(@Param(value = "sender") MovieSenderDTO sender, @Param("path") String path);

    String getMoviePath(Long movie_uid);


    MovieSenderDTO getMovie(Long movie_uid);

    void setMovie(Long movie_uid);

    List<MovieDTO> findMovieByMemberTo(Long member_to);

    int getOneMovie(Long movie_uid) throws Exception;


// <select id="findMinDate" parameterType="long" resultType="long">
    Optional<MovieOccur> findMinDate(Long user_uid) throws Exception;

// <select id="findMovieCount" parameterType="hashMap" resultType="int">
    int findMovieCount(Map<String,Object> map) throws Exception;

}
