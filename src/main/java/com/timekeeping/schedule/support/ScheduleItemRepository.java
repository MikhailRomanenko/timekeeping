package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.timekeeping.employee.Employee;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.ScheduleItemId;
import com.timekeeping.schedule.WorkType;

/**
 * Spring Data repository for {@link ScheduleItem} entities.
 * For more information see {@link JpaRepository}.
 * 
 * @author Mikhail Romanenko
 *
 */

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, ScheduleItemId> {

	@Query("select new com.timekeeping.schedule.support.ScheduleItemRepository.WorkingTime(si.duration, si.type) from ScheduleItem si where si.employee=?1 and si.schedule.date between ?2 and ?3")
	List<WorkingTime> findWorkingTimeByEmployeeAndDateRange(Employee employee, LocalDate from, LocalDate to);
	
	/**
	 * Class for query projection purpose. Used in {@link ScheduleItemRepository} to provide query 
	 * projection return type.
	 * 
	 * @author Mikhail Romanenko
	 *
	 */
	static class WorkingTime {
		private final int duration;
		private final WorkType type;
		
		WorkingTime(int duration, WorkType type) {
			this.duration = duration;
			this.type = type;
		}
		
		int getDuration() {
			return duration;
		}
		
		WorkType getType() {
			return type;
		}
	}
}
