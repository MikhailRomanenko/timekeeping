package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.timekeeping.AbstractRepositoryIntegerationTest;


public class ScheduleItemRepositoryITTests extends AbstractRepositoryIntegerationTest {

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
