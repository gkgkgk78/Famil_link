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
    public void addSchedule(Long account_uid, Long memberUid, Schedule schedule) {

        schedule.setAccount_uid(account_uid);
        schedule.setMember_uid(memberUid);

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
    public List<Schedule> findScheduleListForMonth(Long account_uid) {


        return scheduleMapper.selectScheduleListForMonth(account_uid);
    }

    @Override
    public List<Schedule> findScheduleListForMonthTop5(Long account_uid) {
        return scheduleMapper.selectScheduleListForMonthTop5(account_uid);
    }

    @Override
    public void modifySchedule(Long uid, Long memberUid, Schedule schedule) {

        schedule.setUid(uid);
        schedule.setMember_uid(memberUid);

        scheduleMapper.updateSchedule(schedule);

    }

    @Override
    public void removeSchedule(Long uid, Long memberUid) {

        Schedule schedule = new Schedule();
        schedule.setUid(uid);
        schedule.setMember_uid(memberUid);

        scheduleMapper.deleteSchedule(schedule);

    }
}
