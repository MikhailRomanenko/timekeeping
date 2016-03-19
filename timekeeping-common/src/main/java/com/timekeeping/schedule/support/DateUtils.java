package com.timekeeping.schedule.support;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Utility class for {@link LocalDate} objects.
 * 
 * @author Mikhail Romanenko
 *
 */
public class DateUtils {
	
	/**
	 * Returns {@link LocalDate} date of the Monday of week to which provided {@code date} belongs to.
	 */
	public static LocalDate getWeekStart(LocalDate date) {
		int daysToMonday = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
		return date.minusDays(daysToMonday);
	}
	
	/**
	 * Returns {@link LocalDate} date of the Sunday of week to which provided {@code date} belongs to.
	 */
	public static LocalDate getWeekEnd(LocalDate date) {
		int daysToSunday = DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue();
		return date.plusDays(daysToSunday);
	}
	
	/**
	 * Returns {@link LocalDate} date of the first day of month to which provided {@code date} belongs to.
	 */
	public static LocalDate getMonthFirstDay(LocalDate date) {
		return date.withDayOfMonth(1);
	}
	
	/**
	 * Returns {@link LocalDate} date of the last day of month to which provided {@code date} belongs to.
	 */
	public static LocalDate getMonthLastDay(LocalDate date) {
		int daysInMonth = date.getMonth().length(date.isLeapYear());
		return date.withDayOfMonth(daysInMonth);
	}
}
