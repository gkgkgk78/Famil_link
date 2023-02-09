package com.famillink.model.service;

import com.famillink.model.domain.user.Member;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    String makeTimeIncludeUid(Long member_to); //시간순으로 member에게 온 영상을 구분하기 위해

    void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data); //emitter send

    boolean hasLostData(String lastEventId); //유실되는 데이트 검사 후 방지

    void sendLostData(String lastEventId, Long member_to, String emitterId, SseEmitter emitter);

    SseEmitter subscribe(Long member_to, String lastEventId); //구독

    //다른 사용자가 알림을 보내는 기능 (영상 전송 버튼 누륵고 잘 도착했다는 HttpStatus.Ok뜨면)
    //일단 알림을 구성하고, 해당 알림에 대한 이벤트를 발생시킴
        // 어떤 회원에게 알림을 보낼지에 대해 찾음
        // 알림을 받을 회원의 emitter를 찾아 해당 Emitter로 Send
    void send(Long member_to, String content, String url);

}
