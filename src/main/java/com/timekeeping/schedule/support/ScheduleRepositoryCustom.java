package com.timekeeping.schedule.support;

import com.timekeeping.schedule.Schedule;

/**
 * Interface to provide custom implementation of the {@link ScheduleRepository} 
 * Spring Data based repository.
 * 
 * @author Mikhail Romanenko
 *
 */
public interface ScheduleRepositoryCustom {
	void saveOrUpdate(Schedule schedule);
}
