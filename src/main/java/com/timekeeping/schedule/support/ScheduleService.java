package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.employee.Employee;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;
import com.timekeeping.shop.Shop;

/**
 * Service providing high-level data access and other {@link Schedule}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */

@Service
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleItemRepository scheduleItemRepository;
	private final BreakPolicy breakPolicy;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ScheduleItemRepository scheduleItemRepository, BreakPolicy breakPolicy) {
		this.scheduleRepository = scheduleRepository;
		this.scheduleItemRepository = scheduleItemRepository;
		this.breakPolicy = breakPolicy;
	}

	/**
	 * Finds particular {@link Schedule} entity corresponding to the given
	 * {@code shop} and {@code date} parameters.
	 * 
	 * @param shop
	 *            {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule.
	 * @return {@link Schedule} entity, otherwise returns {@code null}.
	 */
	public Schedule findSchedule(Shop shop, LocalDate date) {
		return scheduleRepository.findByShopAndDate(shop, date);
	}

	/**
	 * Saves {@link Schedule} object or updates its {@link ScheduleItem} set
	 * with provided {@code schedule} object.
	 * 
	 * @param schedule
	 *            {@link Schedule} object to save.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public void saveSchedule(Schedule schedule) {
		// TODO Test logic
		if (schedule.getId() != null) {
			updateItems(schedule);
		} else {
			scheduleRepository.save(schedule);
		}

	}

	/**
	 * Updates {@link ScheduleItem}s set of the existing {@link Schedule} entity
	 * with provided {@code schedule}'s set.
	 * 
	 * @param schedule
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected void updateItems(Schedule schedule) {
		// TODO Test logic
		Schedule scheduleToUpdate = scheduleRepository.findOne(schedule.getId());
		scheduleToUpdate.updateItems(schedule.getItems());
		scheduleRepository.save(scheduleToUpdate);
	}

	/**
	 * Removes particular {@link Schedule} entity corresponding to the given
	 * {@code shop} and {@code date} parameters.
	 * 
	 * @param shop
	 *            {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule.
	 */
	public void removeSchedule(Shop shop, LocalDate date) {
		// TODO Test logic
		scheduleRepository.deleteByShopAndDate(shop, date);
	}

	/**
	 * Calculates statistic for given employee and date.
	 * All calculations are made according to the provided date.
	 * @param date calculations are made according to this date
	 * @param employee calculation are made for this employee
	 * @return {@link WorkStatistic} object representing statistic
	 */
	@Transactional(readOnly = true)
	public WorkStatistic getStatistic(LocalDate date, Employee employee) {
		// TODO Test logic
		int timeForWeek = getWorkingTime(employee, DateUtils.getMonthFirstDay(date), DateUtils.getMonthLastDay(date), breakPolicy.withoutBreak());
		int timeWorked = getWorkingTime(employee, DateUtils.getMonthFirstDay(date), date, breakPolicy.withoutBreak());
		int timeForMonth = getWorkingTime(employee, DateUtils.getWeekStart(date), DateUtils.getWeekEnd(date), breakPolicy.withoutBreak());
		return new WorkStatistic(date, employee.getId(), timeForWeek, timeWorked, timeForMonth);
	}

	/**
	 * Returns working time for specified period and filtered by provided {@link breakMapper}.
	 * @param employee time fetched for this employee
	 * @param from date from which fetching is occurred
	 * @param to date to which fetching is occurred
	 * @param breakMapper maps working hours
	 * @return working time in minutes
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	protected int getWorkingTime(Employee employee, LocalDate from, LocalDate to, UnaryOperator<Integer> breakMapper) {
		List<WorkingTime> days = scheduleItemRepository.findWorkingTimeByEmployeeAndDateRange(employee, from, to);
		int time = days.stream()
					.filter(d -> d.getType() == WorkType.WORK)
					.map(WorkingTime::getDuration)
					.map(breakMapper)
					.collect(Collectors.summingInt(d -> d));
		return time;
	}
	
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
