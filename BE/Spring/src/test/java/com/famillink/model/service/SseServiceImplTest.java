package com.famillink.model.service;

import com.famillink.model.domain.user.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SseServiceImplTest {

    @Autowired
    SseService sseService;

    @Test
    @DisplayName("알림 구독을 진행한다")
    public void subscribe() throws Exception {
        //given
        Member member = new Member(1L, 2L, "다혜", "다");
        String lastEventId = "";


        //when, then
        Assertions.assertDoesNotThrow(()->sseService.subscribe(member.getUid(), lastEventId));
    }

    @Test
    @DisplayName("알림 메세지를 전송한다")
    public void send() throws Exception {
        //given
        Member member = new Member(1L, 2L, "다혜", "다");
        String lastEventId = "";
        sseService.subscribe(member.getUid(), lastEventId);

        //when, then
        Assertions.assertDoesNotThrow(() -> sseService.send(member.getUid(), "new video", "localhotst:9999/movie"));
    }

}
