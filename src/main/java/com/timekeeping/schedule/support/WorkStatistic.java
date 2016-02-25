package com.timekeeping.schedule.support;

import java.time.LocalDate;

/**
 * Domain class for an employee statistic. This statistic includes scheduled
 * time to work for week as well as for month, and time already worked since a
 * beginning of the month. All data are measured in minutes.
 * 
 * @author Mikhail Romanenko
 *
 */
public class WorkStatistic {
	private LocalDate date;
	private long employeeId;
	private int timeForWeek;
	private int timeWorked;
	private int timeForMonth;

	public WorkStatistic(LocalDate date, long employeeId, int timeForWeek, int timeWorked, int timeForMonth) {
		this.date = date;
		this.employeeId = employeeId;
		this.timeForWeek = timeForWeek;
		this.timeWorked = timeWorked;
		this.timeForMonth = timeForMonth;
	}
	
	public LocalDate getDate() {
		return this.date;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	/**
	 * Returns time arranged to work for week.
	 */
	public int getTimeForWeek() {
		return timeForWeek;
	}

	/**
	 * Returns time already worked up to date.
	 */
	public int getTimeWorked() {
		return timeWorked;
	}

	/**
	 * Returns time arranged to work for month.
	 */
	public int getTimeForMonth() {
		return timeForMonth;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkStatistic [employeeId: ").append(employeeId).append(", timeForWeek: ").append(timeForWeek)
				.append(", timeWorked: ").append(timeWorked).append(", timeForMonth: ").append(timeForMonth)
				.append("]");
		return builder.toString();
	}

}
