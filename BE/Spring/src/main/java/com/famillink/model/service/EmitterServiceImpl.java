package com.famillink.model.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class EmitterServiceImpl implements EmitterService{

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacherId, Object event) {
        eventCache.put(eventCacherId, event);

    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberUid(String member_to) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(member_to))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //Collectors의 toMap() 메서드를 사용하여 Stream을 Map인스턴스로 수집

    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberUid(String member_to) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(member_to))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithMemberUid(String member_to) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(member_to)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(String member_to) {
        eventCache.forEach(
                (key, emitters) -> {
                    if (key.startsWith(member_to)) {
                        eventCache.remove(key);
                    }
                }
        );

    }
}
