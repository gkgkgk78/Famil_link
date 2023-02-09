package com.famillink.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

public class EmitterServiceImplTest {

    private EmitterService emitterService = new EmitterServiceImpl();
    private Long DEFAULT_TIMEOUT = 60L * 1000 * 60L; //1시간

    @Test
    @DisplayName("새로운 Emitter를 추가한다")
    public void save() throws Exception {
        //given
        Long member_to = 1L;
        String emitterId = member_to + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterService.save(emitterId, sseEmitter));
    }

    @Test
    @DisplayName("수신한 이벤트를 캐시에 저장한다")
    public void saveEventCache() throws Exception {
        //given
        Long member_to = 1L;
        String eventCacheId = member_to + "_" + System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "영상이 도착했습니다");

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterService.saveEventCache(eventCacheId, response));

    }

    @Test
    @DisplayName("어떤 회원이 접속한 모든 Emitter를 찾는다")
    public void findAllEmitterStartWithByMemberUid() throws Exception {
        //given
        Long member_to = 1L;
        String emitterId1 = member_to + "_" + System.currentTimeMillis(); //첫번째 emitter
        emitterService.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = member_to + "_" + System.currentTimeMillis(); //첫번째 emitter
        emitterService.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId3 = member_to + "_" + System.currentTimeMillis(); //첫번째 emitter
        emitterService.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        Map<String, SseEmitter> ActualResult = emitterService.findAllEmitterStartWithByMemberUid(String.valueOf(member_to));

        //then : 등록된 emitter를 다 찾는지 비교
        Assertions.assertEquals(3, ActualResult.size());
    }


    @Test
    @DisplayName("수신한 이벤트를 캐시에서 모두 찾는다")
    public void findAllEventCacheStartWithByMemberUid() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("msg1", "영상이 도착했습니다");

        //given
        Long member_to = 1L;

        String eventCacheId1 = member_to + "_" + System.currentTimeMillis();
        emitterService.saveEventCache(eventCacheId1, response);

        Thread.sleep(100);
        String eventCacheId2 = member_to + "_" + System.currentTimeMillis();
        emitterService.saveEventCache(eventCacheId2, response);

        Thread.sleep(100);
        String eventCacheId3 = member_to + "_" + System.currentTimeMillis();
        emitterService.saveEventCache(eventCacheId3, response);

        //when
        Map<String, Object> ActualResult = emitterService.findAllEventCacheStartWithByMemberUid(String.valueOf(member_to));

        //then
        Assertions.assertEquals(3, ActualResult.size());
    }

    @Test
    @DisplayName("ID를 통해 Emitter를 Repository에서 제거한다")
    public void deleteByMemberUid() throws Exception {
        //given
        Long member_to = 1L;
        String emitterId = member_to + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when
        emitterService.save(emitterId, sseEmitter);
        emitterService.deleteById(emitterId);

        //then
        Assertions.assertEquals(0, emitterService.findAllEmitterStartWithByMemberUid(emitterId).size());
    }

    @Test
    @DisplayName("저장된 모든 Emitter를 제거한다")
    public void deleteAllEmitterStartWithMemberUid() throws Exception {
        //given
        Long member_to = 1L;
        String emitterId1 = member_to + "_" + System.currentTimeMillis();
        emitterService.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2  = member_to + "_" + System.currentTimeMillis();
        emitterService.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));


        //when
        emitterService.deleteAllEmitterStartWithMemberUid(String.valueOf(member_to));

        //then
        Assertions.assertEquals(0, emitterService.findAllEmitterStartWithByMemberUid(String.valueOf(member_to)).size());
    }

    @Test
    @DisplayName("저장된 모든 캐시를 삭제한다")
    public void deleteAllEventCacheStartWithId() throws Exception{
        Map<String, Object> response = new HashMap<>();

        response.put("msg", "영상이 도착했습니다");

        //given
        Long member_to = 1L;

        String eventChacheId1 = member_to + "_" + System.currentTimeMillis();
        emitterService.saveEventCache(eventChacheId1, response);

        Thread.sleep(100);
        String eventChacheId2 = member_to + "_" + System.currentTimeMillis();
        emitterService.saveEventCache(eventChacheId2, response);


        //when
        emitterService.deleteAllEventCacheStartWithId(String.valueOf(member_to));


        //then
        Assertions.assertEquals(0, emitterService.findAllEventCacheStartWithByMemberUid(String.valueOf(member_to)).size());


    }
}
