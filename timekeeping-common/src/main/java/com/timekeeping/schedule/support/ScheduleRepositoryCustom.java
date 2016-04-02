package com.timekeeping.schedule.support;

import java.time.LocalDate;

/**
 * Interface to provide custom implementation of the {@link ScheduleRepository} 
 * Spring Data based repository.
 * 
 * @author Mikhail Romanenko
 *
 */
public interface ScheduleRepositoryCustom {
	
	/**
	 * Determine whether or not schedule with provided data exists.
	 * @param shopId 
	 * @param date
	 * @return {@code true} if exist otherwise {@code false}
	 */
	boolean exists(Long shopId, LocalDate date);
}
