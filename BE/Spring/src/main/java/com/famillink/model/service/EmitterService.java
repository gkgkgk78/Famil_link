package com.famillink.model.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterService {
    SseEmitter save(String emitterId, SseEmitter sseEmitter); //Emitter 저장
    void saveEventCache(String eventCacheId, Object event); //Event 저장

    Map<String, SseEmitter> findAllEmitterStartWithByMemberUid(String member_to); //해당 회원과 관련된 모든 Emitter를 찾는다

    Map<String, Object> findAllEventCacheStartWithByMemberUid(String member_to); //해당 회원과 관련된 모든 이벤트를 찾는다

    void deleteById(String emitterId); //Emitter를 지운다

    void deleteAllEmitterStartWithMemberUid(String member_to); //해당 회원과 관련된 모든 Emitter를 지운다

    void deleteAllEventCacheStartWithId(String member_to); //해당 회원과 관련된 모든 이벤트를 지운다
}
