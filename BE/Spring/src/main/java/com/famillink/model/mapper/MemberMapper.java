package com.famillink.model.mapper;

import com.famillink.model.domain.user.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<Member> findUserByUserId(String id) throws Exception;

    Optional<Member> findUserByUid(Long uid);

    void signup(Member member) throws Exception;

    void setRefreshToken(Member userDTO) throws Exception;

    void setSalt(@Param(value = "uid") Long uid, @Param(value = "salt") String salt) throws Exception;

    String getSalt(Long uid);

    void checkEmail(Long uid) throws Exception;

    void setPassword(@Param(value = "email") String email, @Param(value = "pw") String pw);

    Optional<Member> findUserByNickname(String nickname);

}
