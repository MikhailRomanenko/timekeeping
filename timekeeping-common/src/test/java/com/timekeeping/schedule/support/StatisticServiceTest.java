package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;

public class StatisticServiceTest {
	private static final LocalDate DATE = LocalDate.now();
	private static final Long EMPLOYEE_ID = 1L;
	private ScheduleItemRepository repository;
	private BreakPolicy policy;
	private StatisticService service;

	@Before
	public void setUp() {
		repository = mock(ScheduleItemRepository.class);
		policy = mock(BreakPolicy.class);
		given(policy.mapper()).willReturn(UnaryOperator.identity());
		service = new StatisticService(repository, policy);
	}

	@Test
	public void workingTimeOnly() {
		given(repository.findWorkingTimeByEmployeeIdAndDateRange(any(), any(), any()))
				.willReturn(Arrays.asList(new ScheduledDay(0, 10, "WORK"), new ScheduledDay(0, 10, "WORK"),
						new ScheduledDay(0, 10, "WORK"), new ScheduledDay(10, 10, "WORK"),
						new ScheduledDay(0, 10, "WORK")));
		int result = service.getWorkingTimeStatistic(DATE, EMPLOYEE_ID).getTimeForMonth();
		assertThat(result, equalTo(50));
	}

	@Test
	public void workingTimeAndOtherTypes() {
		given(repository.findWorkingTimeByEmployeeIdAndDateRange(any(), any(), any()))
				.willReturn(Arrays.asList(new ScheduledDay(0, 10,"ABSENT"), new ScheduledDay(0, 10, "VACATION"),
						new ScheduledDay(0, 10, "SICK"), new ScheduledDay(0, 10, "WORK"),
						new ScheduledDay(0, 10, "WORK")));
		int result = service.getWorkingTimeStatistic(DATE, EMPLOYEE_ID).getTimeForMonth();
		assertThat(result, equalTo(20));
	}

	@Test
	public void workingTimeEmpty() {
		given(repository.findWorkingTimeByEmployeeIdAndDateRange(any(), any(), any()))
				.willReturn(Collections.emptyList());
		int result = service.getWorkingTimeStatistic(DATE, EMPLOYEE_ID).getTimeForMonth();
		assertThat(result, equalTo(0));
	}
}
