package com.famillink.controller;

import com.famillink.annotation.ValidationGroups;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.domain.user.Schedule;
import com.famillink.model.service.ScheduleService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Api("Schedule Controller")
@RequiredArgsConstructor
@RequestMapping("/schedule")
@JsonAutoDetect
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    //account login 이후 조회 이루어짐
    @ApiOperation(value = "이번달 전체 일정 조회", notes = "이번달 전체 일정을 조회합니다")
    @GetMapping("/list")
    public ResponseEntity<?> getFromTodayToMonthSchedule(Authentication authentication){

        Account account = (Account) authentication.getPrincipal();

        Long account_uid = account.getUid();

        //test
        //Long account_uid = 13L;

        List<Schedule> scheduleList = scheduleService.findScheduleListForMonth(account_uid);



        if (scheduleList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("등록된 일정이 없습니다");
        }

        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String, Object>() {{
            put("list", scheduleList);
        }});
    }

    @ApiOperation(value = "일정 5개 조회", notes = "이번달 전체 일정 중 최근 5개의 일정 조회")
    @GetMapping("/list/5")
    public ResponseEntity<?> getMonthSchedule(Authentication authentication){

        Account account = (Account) authentication.getPrincipal();

        Long account_uid = account.getUid();

        //test
        //Long account_uid = 13L;

        List<Schedule> scheduleList = scheduleService.findScheduleListForMonthTop5(account_uid);



        if (scheduleList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("등록된 일정이 없습니다");
        }

        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String, Object>() {{

            put("topList", scheduleList);

        }});

    }

    //member login 이후 등록, 수정, 삭제 이루어짐
    @ApiOperation(value = "일정 등록", notes = "일정 등록하는 컨트롤러입니다. req_data : [ context, date(조회를 원하는 일정의 날짜) ]")
    @PostMapping("/regist")
    public ResponseEntity<?> addSchedule(Authentication authentication, @RequestBody @Validated(ValidationGroups.regist.class) Schedule schedule) {

        Member member = (Member) authentication.getPrincipal();


        Long accountUid = member.getUser_uid();
        Long memberUid = member.getUid();

        //Test
//        Long accountUid = schedule.getAccount_uid();
//        Long memberUid = schedule.getMember_uid();

        scheduleService.addSchedule(accountUid, memberUid, schedule);

        return ResponseEntity.status(HttpStatus.OK).body("스케줄 등록에 성공했습니다");
    }


    @ApiOperation(value = "일정 수정", notes = "req_data : [uid(글 번호), context(수정내용), date(일정 예정일 수정)] \n 글을 등록한 멤버만 수정할 수 있습니다")
    @PutMapping("/reschedule/{uid}")
    public ResponseEntity<?> rearrangeSchedule(Authentication authentication, @PathVariable Long uid, @RequestBody @Validated(ValidationGroups.regist.class) Schedule schedule) {

        Member member = (Member) authentication.getPrincipal();

        Long memberUid = member.getUid();


        //변경 사항이 없는데 요청이 올 경우
        Schedule compareSchedule = scheduleService.findSchedule(uid);
        if (schedule.getContent().equals(compareSchedule.getContent()) && schedule.getDate().toString().equals(compareSchedule.getDate().toString())) {
            return ResponseEntity.status(HttpStatus.OK).body("수정 사항이 없습니다");
        }
        //test
//        Long memberUid = 7L;
        scheduleService.modifySchedule(uid, memberUid, schedule);
        return ResponseEntity.status(HttpStatus.OK).body("일정이 수정되었습니다");
    }


    @ApiOperation(value = "일정 삭제", notes = "req_data : [uid(글 번호)] \n 글을 등록한 멤버만 삭제할 수 있습니다")
    @DeleteMapping("/remove/{uid}")
    public ResponseEntity<?> deleteSchedule(Authentication authentication, @PathVariable Long uid) {

        Member member = (Member) authentication.getPrincipal();

        Long memberUid = member.getUid();

        //test
//        Long memberUid = 7L;

        if (scheduleService.findSchedule(uid) == null) {
            throw new BaseException(ErrorMessage.NOT_CORRECT_INFORMATION);
        }

        scheduleService.removeSchedule(uid, memberUid);

        return ResponseEntity.status(HttpStatus.OK).body("일정이 삭제되었습니다");

    }


}
