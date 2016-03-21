package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;

public class StatisticServiceTest {
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
				.willReturn(Arrays.asList(new WorkingTime(10, "WORK"), new WorkingTime(10, "WORK"),
						new WorkingTime(10, "WORK"), new WorkingTime(10, "WORK"),
						new WorkingTime(10, "WORK")));
		int result = service.getWorkingTime(null, null, null, policy.mapper());
		assertThat(result, equalTo(50));
	}

	@Test
	public void workingTimeAndOtherTypes() {
		given(repository.findWorkingTimeByEmployeeIdAndDateRange(any(), any(), any()))
				.willReturn(Arrays.asList(new WorkingTime(10,"ABSENT"), new WorkingTime(10, "VACATION"),
						new WorkingTime(10, "SICK"), new WorkingTime(10, "WORK"),
						new WorkingTime(10, "WORK")));
		int result = service.getWorkingTime(null, null, null, policy.mapper());
		assertThat(result, equalTo(20));
	}

	@Test
	public void workingTimeEmpty() {
		given(repository.findWorkingTimeByEmployeeIdAndDateRange(any(), any(), any()))
				.willReturn(Collections.emptyList());
		int result = service.getWorkingTime(null, null, null, policy.mapper());
		assertThat(result, equalTo(0));
	}
}
