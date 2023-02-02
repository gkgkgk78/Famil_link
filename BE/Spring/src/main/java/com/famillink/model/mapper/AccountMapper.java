package com.famillink.model.mapper;

import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AccountMapper {
    Optional<Account> findUserByEmail(String email) throws Exception;

    Optional<Account> findUserByUid(Long uid);

    // 전체 가족 구성원 조회 : 편지 보낼 때 구성원 선택 시 필요
    List<Member> findUserFamily(String email) throws Exception;

    void signup(Account account) throws Exception;

    void setRefreshToken(Account account) throws Exception;

    void setSalt(@Param(value = "uid") Long uid, @Param(value = "salt") String salt) throws Exception;

    String getSalt(Long uid);

    void checkEmail(Long uid) throws Exception;

    void setPassword(@Param(value = "email") String email, @Param(value = "pw") String pw);


}
