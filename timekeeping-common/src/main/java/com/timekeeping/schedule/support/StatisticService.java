package com.timekeeping.schedule.support;
import java.time.LocalDate;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.schedule.WorkType;
import com.timekeeping.schedule.support.BreakPolicy;
import com.timekeeping.schedule.support.DateUtils;
import com.timekeeping.schedule.support.ScheduleItemRepository;
import com.timekeeping.schedule.support.WorkStatistic;

/**
 * Service class that provides different kinds of the statistical data.
 * @author Mikhail Romanenko
 *
 */
@Transactional(readOnly = true)
public class StatisticService {
	
	private final ScheduleItemRepository scheduleItemRepository;
	private final BreakPolicy breakPolicy;
	
	@Autowired
	public StatisticService(ScheduleItemRepository scheduleItemRepository, BreakPolicy breakPolicy) {
		this.scheduleItemRepository = scheduleItemRepository;
		this.breakPolicy = breakPolicy;
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
