package com.famillink.model.service;

import com.famillink.model.domain.user.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SseServiceImpl implements SseService{

    @Autowired
    private EmitterService emitterService;


    private Long timeout = 60L * 1000 * 60L; //1시간

    @Override
    public String makeTimeIncludeUid(Long member_to) {
        return member_to + "_" + System.currentTimeMillis();
    }

    @Override
    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try{

            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));

        } catch (Exception e){
            emitterService.deleteById(emitterId);
        }
    }

    @Override
    public boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    @Override
    public void sendLostData(String lastEventId, Long member_to, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterService.findAllEventCacheStartWithByMemberUid(String.valueOf(member_to));

        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Override
    public SseEmitter subscribe(Long member_to, String lastEventId) {
        String emitterId = makeTimeIncludeUid(member_to);
        SseEmitter emitter = emitterService.save(emitterId, new SseEmitter(timeout));

        //시간초과나 네트워크 에러 발생시
        emitter.onCompletion(() -> emitterService.deleteById(emitterId));
        emitter.onTimeout(() -> emitterService.deleteById(emitterId));

        //503에러 방지를 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeUid(member_to);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [member uid="+member_to+"]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if(hasLostData(lastEventId)){
            sendLostData(lastEventId, member_to, emitterId, emitter);
        }

        return emitter;
    }

    @Override
    public void send(Member reciever, String content, String url) {
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "new video");

        String receiverId = String.valueOf(reciever.getUid());
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterService.findAllEmitterStartWithByMemberUid(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterService.saveEventCache(key, response);
                    sendNotification(emitter, eventId, key, response);
                }
        );


    }


}

