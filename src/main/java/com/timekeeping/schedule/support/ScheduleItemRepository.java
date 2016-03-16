package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.ScheduleItemId;

/**
 * Spring Data repository for {@link ScheduleItem} entities.
 * For more information see {@link JpaRepository}.
 * 
 * @author Mikhail Romanenko
 *
 */

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, ScheduleItemId> {

	@Query("select new com.timekeeping.schedule.support.WorkingTime(si.duration, si.type) from ScheduleItem si where si.employee.id=?1 and si.schedule.date between ?2 and ?3")
	List<WorkingTime> findWorkingTimeByEmployeeIdAndDateRange(Long employeeId, LocalDate from, LocalDate to);

}
