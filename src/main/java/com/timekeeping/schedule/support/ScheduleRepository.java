package com.timekeeping.schedule.support;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;

/**
 * Spring Data repository for {@link Schedule} entities.
 * For more information see {@link JpaRepository}.
 * 
 * @author Mikhail Romanenko
 *
 */

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	Schedule findByShopAndDate(Shop shop, LocalDate date);
	
	@Modifying
	@Query("delete Schedule s where s.shop=?1 and s.date=?2")
	void deleteByShopAndDate(Shop shop, LocalDate date);
}
