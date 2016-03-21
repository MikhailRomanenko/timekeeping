package com.timekeeping.schedule.support;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.schedule.WorkType;
import com.timekeeping.schedule.support.BreakPolicy;
import com.timekeeping.schedule.support.DateUtils;
import com.timekeeping.schedule.support.ScheduleItemRepository;
import com.timekeeping.schedule.support.WorkingTimeStatistic;

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
	 * @return {@link WorkingTimeStatistic} object representing statistic
	 */
	public WorkingTimeStatistic getWorkingTimeStatistic(LocalDate date, Long employeeId) {
		final Predicate<? super ScheduledDay> filter = d -> d.getType().equals(WorkType.WORK);
		int timeForWeek = getDurationTime(employeeId, DateUtils.getWeekStart(date), DateUtils.getWeekEnd(date),
				filter ,breakPolicy.mapper());
		int timeWorked = getDurationTime(employeeId, DateUtils.getMonthFirstDay(date), date, filter, breakPolicy.mapper());
		int timeForMonth = getDurationTime(employeeId, DateUtils.getMonthFirstDay(date), DateUtils.getMonthLastDay(date),
				filter, breakPolicy.mapper());
		return new WorkingTimeStatistic(date, employeeId, timeForWeek, timeWorked, timeForMonth);
	}

	private int getDurationTime(Long employeeId, LocalDate from, LocalDate to,
			Predicate<? super ScheduledDay> filter, UnaryOperator<Integer> mapper) {
		List<ScheduledDay> days = scheduleItemRepository.findWorkingTimeByEmployeeIdAndDateRange(employeeId, from, to);
		int time = days.stream().filter(filter).map(ScheduledDay::getDuration)
				.map(mapper).collect(Collectors.summingInt(d -> d));
		return time;
	}
}

class ScheduledDay {
	private final int start;
	private final int duration;
	private final WorkType type;

	public ScheduledDay(int start, int duration, String type) {
		this.start = start;
		this.duration = duration;
		this.type = WorkType.valueOf(type);
	}

	public ScheduledDay(int start, int duration, WorkType type) {
		this.start = start;
		this.duration = duration;
		this.type = type;
	}
	
	int getStartTimr() {
		return start;
	}

	int getDuration() {
		return duration;
	}

	WorkType getType() {
		return type;
	}
}
