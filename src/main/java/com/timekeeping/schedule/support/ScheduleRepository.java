package com.timekeeping.schedule.support;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.schedule.Schedule;

/**
 * Spring Data repository for {@link Schedule} entities.
 * For more information see {@link JpaRepository}.
 * 
 * @author Mikhail Romanenko
 *
 */

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {
	/**
	 * Find particular {@link Schedule} by specified ship id and date.
	 * @param shopId id value of the {@link Shop}
	 * @param date date of the schedule to return
	 * @return
	 */
	Schedule findByShopIdAndDate(Long shopId, LocalDate date);
	
	/**
	 * Delete particular {@link Schedule} by specified ship id and date.
	 * @param shopId id value of the {@link Shop}
	 * @param date date of the schedule to delete
	 */
	@Modifying
	@Transactional(readOnly = false)
	@Query("delete Schedule s where s.shop.id=?1 and s.date=?2")
	void deleteByShopIdAndDate(Long shopId, LocalDate date);

	/**
	 * Find version number of the particular {@link Schedule} by the specified {@code schedule id}. 
	 * @return version number
	 */
	@Query("select s.version from Schedule s where s.id=?1")
	int getVersionFor(long scheduleId);
}
