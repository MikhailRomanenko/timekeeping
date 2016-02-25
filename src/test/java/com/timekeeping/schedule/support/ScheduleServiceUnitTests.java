package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.timekeeping.schedule.WorkType;
import com.timekeeping.schedule.support.ScheduleItemRepository.WorkingTime;

public class ScheduleServiceUnitTests {
	private ScheduleItemRepository repository;
	private ScheduleService service;

	@Before
	public void setUp() {
		repository = mock(ScheduleItemRepository.class);
		service = new ScheduleService(null, repository, null);
	}

	@Test
	public void workingTimeOnly() {
		given(repository.findWorkingTimeByEmployeeAndDateRange(any(), any(), any()))
				.willReturn(Arrays.asList(new WorkingTime(10, WorkType.WORK), new WorkingTime(10, WorkType.WORK),
						new WorkingTime(10, WorkType.WORK), new WorkingTime(10, WorkType.WORK),
						new WorkingTime(10, WorkType.WORK)));
		int result = service.getWorkingTime(null, null, null, c -> c);
		assertThat(result, equalTo(50));
	}

	@Test
	public void workingTimeAndOtherTypes() {
		given(repository.findWorkingTimeByEmployeeAndDateRange(any(), any(), any()))
				.willReturn(Arrays.asList(new WorkingTime(10, WorkType.ABSENT), new WorkingTime(10, WorkType.VACATION),
						new WorkingTime(10, WorkType.SICK), new WorkingTime(10, WorkType.WORK),
						new WorkingTime(10, WorkType.WORK)));
		int result = service.getWorkingTime(null, null, null, c -> c);
		assertThat(result, equalTo(20));
	}

	@Test
	public void workingTimeEmpty() {
		given(repository.findWorkingTimeByEmployeeAndDateRange(any(), any(), any()))
				.willReturn(Collections.emptyList());
		int result = service.getWorkingTime(null, null, null, c -> c);
		assertThat(result, equalTo(0));
	}
}
