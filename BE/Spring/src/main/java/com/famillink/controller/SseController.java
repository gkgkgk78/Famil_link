package com.famillink.controller;

import com.famillink.model.domain.user.Member;
import com.famillink.model.service.SseService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequiredArgsConstructor
public class SseController {

    private SseService sseService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(Authentication authentication, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){

        Member member = (Member) authentication.getPrincipal();

        return sseService.subscribe(member.getUid(), lastEventId);
    }


    @ApiOperation(value = "영상 전송 시 이벤트 발생", notes = "영상 받는 사람에게 영상이 전송됐다는 이벤트 발생을 알린다.")
    @GetMapping(value = "/check", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public void check(Long member_to, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){

        sseService.send(member_to, "new video", lastEventId);


    }




}