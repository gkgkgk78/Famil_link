package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Schedule;
import com.famillink.model.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleMapper scheduleMapper;

    @Transactional
    @Override
    public void addSchedule(Long accountUid, Long memberUid, Schedule schedule) {

        schedule.setAccountUid(accountUid);
        schedule.setMemberUid(memberUid);

        scheduleMapper.insertSchedule(schedule);

    }

    @Override
    public Schedule findSchedule(Long uid) {

        Schedule schedule = scheduleMapper.selectSchedule(uid)
                .orElseThrow(()->new BaseException(ErrorMessage.NOT_FOUND_SCHEDULE));

        return schedule;

    }

    @Override
    public List<Schedule> findScheduleList(Schedule schedule) {

        return scheduleMapper.selectScheduleList(schedule);

    }

    @Override
    public List<Schedule> findScheduleListByMemberUid(Long memberUid) {

        return scheduleMapper.selectScheduleListByMemberUid(memberUid);
    }

    @Override
    public List<Schedule> findScheduleListByDate(Long accountUid, Date date) {

        Schedule schedule = new Schedule();
        schedule.setAccountUid(accountUid);
        schedule.setDate(date);

        return scheduleMapper.selectScheduleListByDate(schedule);
    }

    @Override
    public void modifySchedule(Long uid, Long memberUid, Schedule schedule) {

        schedule.setUid(uid);
        schedule.setMemberUid(memberUid);

        scheduleMapper.updateSchedule(schedule);

    }

    @Override
    public void removeSchedule(Long uid, Long memberUid) {

        Schedule schedule = new Schedule();
        schedule.setUid(uid);
        schedule.setMemberUid(memberUid);

        scheduleMapper.deleteSchedule(schedule);

    }
}
