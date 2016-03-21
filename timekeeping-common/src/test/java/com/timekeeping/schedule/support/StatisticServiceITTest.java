package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.timekeeping.AbstractRepositoryIntegerationTest;

public class StatisticServiceITTest extends AbstractRepositoryIntegerationTest {

	private StatisticService service;
	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	private BreakPolicy policy;
	
	@Before
	public void setUp() {
		policy = mock(BreakPolicy.class);
		given(policy.mapper()).willReturn(UnaryOperator.identity());
		service = new StatisticService(scheduleItemRepository, policy);
	}
	
	@Test
	public void shouldCalculateStatisticTest() {
		WorkingTimeStatistic statistic = service.getWorkingTimeStatistic(LocalDate.of(2016, 3, 1), 2L);
		assertThat(statistic.getTimeWorked(), equalTo(780));
		assertThat(statistic.getTimeForWeek(), equalTo(2340));
		assertThat(statistic.getTimeForMonth(), equalTo(1620));
	}
	
}
