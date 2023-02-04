package com.famillink.model.mapper;

import com.famillink.model.domain.user.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ScheduleMapper {

    //일정 추가
    void insertSchedule(Schedule schedule);

    //일정 조회
    Optional<Schedule> selectSchedule(Long uid);

    List<Schedule> selectScheduleList(Schedule schedule);

    List<Schedule> selectScheduleListByMemberUid(Long memberUid);

    List<Schedule> selectScheduleListByDate(Schedule schedule);

    //일정 수정
    void updateSchedule(Schedule schedule);

    //일정 삭제
    void deleteSchedule(Schedule schedule);

}
