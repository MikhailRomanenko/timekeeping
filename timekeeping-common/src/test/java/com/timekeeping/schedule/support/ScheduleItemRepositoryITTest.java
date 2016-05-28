package com.timekeeping.schedule.support;

import com.timekeeping.AbstractRepositoryIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class ScheduleItemRepositoryITTest extends AbstractRepositoryIntegrationTest {

	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	@Autowired
	private static final LocalDate from = LocalDate.of(2016, 2, 29);
	private static final LocalDate to = LocalDate.of(2016, 3, 2);

	@Test
	public void findWorkingTimeTest() {
		List<Integer> times = scheduleItemRepository
				.findWorkingTimeByEmployeeIdAndDateRange(1L, from, to)
				.stream().map(t -> t.getDuration()).collect(Collectors.toList());
		assertThat(times, hasSize(3));
		assertThat(times, everyItem(equalTo(240)));
	}

}
