package com.famillink.model.service;

import com.famillink.model.domain.user.Schedule;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    //일정 등록
    void addSchedule(Schedule schedule);

    //일정 조회

    Schedule findSchedule(Long uid);

    List<Schedule> findScheduleList(Schedule schedule);

    List<Schedule> findScheduleListByMemberUid(Long memberUid);

    List<Schedule> findScheduleListByDate(Date date);

    //일정 수정
    void modifySchedule(Schedule schedule);

    //일정 삭제
    void removeSchedule(Long uid);

}
