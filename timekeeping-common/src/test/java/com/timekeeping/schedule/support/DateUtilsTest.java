package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void startingEdgeOfMonth() {
		LocalDate date = LocalDate.of(2016, 4, 2);
		
		LocalDate firstDayOfMonth = DateUtils.getMonthFirstDay(date);
		LocalDate lastDayOfMonth = DateUtils.getMonthLastDay(date);
		LocalDate firstDayOfWeek = DateUtils.getWeekStart(date);
		LocalDate lastDayOfWeek = DateUtils.getWeekEnd(date);
		
		assertThat(firstDayOfMonth, equalTo(LocalDate.of(2016, 4, 1)));
		assertThat(lastDayOfMonth, equalTo(LocalDate.of(2016, 4, 30)));
		assertThat(firstDayOfWeek, equalTo(LocalDate.of(2016, 3, 28)));
		assertThat(lastDayOfWeek, equalTo(LocalDate.of(2016, 4, 3)));
	}

	@Test
	public void endingEdgeOfMonth() {
		LocalDate date = LocalDate.of(2016, 4, 25);
		
		LocalDate firstDayOfMonth = DateUtils.getMonthFirstDay(date);
		LocalDate lastDayOfMonth = DateUtils.getMonthLastDay(date);
		LocalDate firstDayOfWeek = DateUtils.getWeekStart(date);
		LocalDate lastDayOfWeek = DateUtils.getWeekEnd(date);
		
		assertThat(firstDayOfMonth, equalTo(LocalDate.of(2016, 4, 1)));
		assertThat(lastDayOfMonth, equalTo(LocalDate.of(2016, 4, 30)));
		assertThat(firstDayOfWeek, equalTo(LocalDate.of(2016, 4, 25)));
		assertThat(lastDayOfWeek, equalTo(LocalDate.of(2016, 5, 1)));
	}
	
	@Test
	public void inTheMiddleOfMonth() {
		LocalDate date = LocalDate.of(2016, 4, 16);
		
		LocalDate firstDayOfMonth = DateUtils.getMonthFirstDay(date);
		LocalDate lastDayOfMonth = DateUtils.getMonthLastDay(date);
		LocalDate firstDayOfWeek = DateUtils.getWeekStart(date);
		LocalDate lastDayOfWeek = DateUtils.getWeekEnd(date);
		
		assertThat(firstDayOfMonth, equalTo(LocalDate.of(2016, 4, 1)));
		assertThat(lastDayOfMonth, equalTo(LocalDate.of(2016, 4, 30)));
		assertThat(firstDayOfWeek, equalTo(LocalDate.of(2016, 4, 11)));
		assertThat(lastDayOfWeek, equalTo(LocalDate.of(2016, 4, 17)));
	}

}
