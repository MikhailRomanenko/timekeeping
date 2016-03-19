package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopRepository;

/**
 * Service providing high-level data access and other {@link Schedule}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */

@Service
@Transactional
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleItemRepository scheduleItemRepository;
	private final ShopRepository shopRepository;
	private final BreakPolicy breakPolicy;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ScheduleItemRepository scheduleItemRepository,
			ShopRepository shopRepository, BreakPolicy breakPolicy) {
		this.scheduleRepository = scheduleRepository;
		this.scheduleItemRepository = scheduleItemRepository;
		this.shopRepository = shopRepository;
		this.breakPolicy = breakPolicy;
	}

	/**
	 * Finds particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shop
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 * @return {@link Schedule} entity, otherwise returns null.
	 */
	@Transactional(readOnly = true)
	public Schedule findSchedule(long shopId, LocalDate date) {
		return scheduleRepository.findByShopIdAndDate(shopId, date);
	}

	/**
	 * Saves {@link Schedule} object or updates its {@link ScheduleItem} set
	 * with provided items.
	 * 
	 * @param date
	 *            {@code LocalDate} of the schedule to save/update.
	 * @param shopId
	 *            id of the shop.
	 * @param items
	 *            {@code Set} of the {@code ScheduleItems} objects to save/update.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveSchedule(Schedule schedule) {
		Assert.notNull(schedule.getDate(), "The 'date' field must not be null");
		Assert.notNull(schedule.getItems(), "The 'items' field must not be null");
		Assert.notNull(schedule.getShop(), "The 'shop' field must not be null");
		Assert.isTrue(shopRepository.exists(schedule.getShop().getId()),
				"Shop with given ID does not exist");
		
		if(!sameVersions(schedule)) {
			throw new OptimisticLockingFailureException("Attempt to save stale copy of an object");
		}
		
		scheduleRepository.saveOrUpdate(schedule);
		
	}
	
	private boolean sameVersions(Schedule schedule) {
		int dbVersion = scheduleRepository.getVersionFor(schedule.getId());
		return schedule.getVersion() == dbVersion;
	}

	/**
	 * Removes particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shopId
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 */
	public void removeSchedule(long shopId, LocalDate date) {
		scheduleRepository.deleteByShopIdAndDate(shopId, date);
	}

	/**
	 * Calculates statistic for given employee and date. All calculations are
	 * made according to the provided date.
	 * 
	 * @param date
	 *            calculations are made according to this date
	 * @param employeeId
	 *            calculation are made for this employee
	 * @return {@link WorkStatistic} object representing statistic
	 */
	@Transactional(readOnly = true)
	public WorkStatistic getStatistic(LocalDate date, Long employeeId) {
		int timeForWeek = getWorkingTime(employeeId, DateUtils.getWeekStart(date), DateUtils.getWeekEnd(date),
				breakPolicy.mapper());
		int timeWorked = getWorkingTime(employeeId, DateUtils.getMonthFirstDay(date), date, breakPolicy.mapper());
		int timeForMonth = getWorkingTime(employeeId, DateUtils.getMonthFirstDay(date), DateUtils.getMonthLastDay(date),
				breakPolicy.mapper());
		return new WorkStatistic(date, employeeId, timeForWeek, timeWorked, timeForMonth);
	}

	int getWorkingTime(Long employeeId, LocalDate from, LocalDate to, UnaryOperator<Integer> breakMapper) {
		List<WorkingTime> days = scheduleItemRepository.findWorkingTimeByEmployeeIdAndDateRange(employeeId, from, to);
		int time = days.stream().filter(d -> d.getType() == WorkType.WORK).map(WorkingTime::getDuration)
				.map(breakMapper).collect(Collectors.summingInt(d -> d));
		return time;
	}

}

class WorkingTime {
	private final int duration;
	private final WorkType type;

	public WorkingTime(int duration, String type) {
		this.duration = duration;
		this.type = WorkType.valueOf(type);
	}

	public WorkingTime(int duration, WorkType type) {
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
