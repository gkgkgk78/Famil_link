package com.famillink.model.mapper;

import com.famillink.model.domain.user.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<Member> findUserByUserId(String id) throws Exception;

    Optional<Member> findUserByUid(Long uid);

    Long findUserByName(String Name);

    void signup(Member member) throws Exception;

    void setRefreshToken(Member userDTO) throws Exception;

    void setSalt(@Param(value = "uid") Long uid, @Param(value = "salt") String salt) throws Exception;

    String getSalt(Long uid);


    Boolean findTogether(Long uid1, Long uid2) throws Exception;


    Optional<Member> findUserByNametoAll(Map<String,Object> map);


    List<Long> getAccount(Long user_uid) throws Exception;


    void deleteMember(Long uid) throws Exception;

}
