package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.RepositoryTestConfig;
import com.timekeeping.schedule.Schedule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RepositoryTestConfig.class)
@TestPropertySource(locations = "classpath:\\test.properties")
public class ScheduleRepositoryITTests {
	private static final LocalDate DATE = LocalDate.of(2016, 3, 1);
	private static final long SHOP_ID = 1L;
	private static final long SCHEDULE_ID = 1L;

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ScheduleItemRepository scheduleItemRepository;

	@Test
	public void findByShopAndDateTest() {
		Schedule schedule = scheduleRepository.findByShopIdAndDate(SHOP_ID, DATE);
		assertThat(schedule.getId(), equalTo(2L));
	}
	
	@Test
	@Transactional
	public void deleteByShopAndDateTest() {
		int count = (int) scheduleRepository.count();
		scheduleRepository.deleteByShopIdAndDate(SHOP_ID, DATE);
		scheduleRepository.flush();
		List<Schedule> schedules = scheduleRepository.findAll();
		assertThat(schedules, hasSize(count - 1));
		assertThat(schedules.stream().map(s -> s.getDate()).collect(Collectors.toList()), 
				not(hasItem(equalTo(DATE))));
	}
	
	@Test
	@Transactional
	public void deletingScheduleShouldDeletesItems() {
		long scheduleItemsCount = scheduleRepository.findOne(SCHEDULE_ID).getItems().size();
		long totalItemsCount = scheduleItemRepository.count();
		scheduleRepository.delete(SCHEDULE_ID);
		scheduleItemRepository.flush();
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount - scheduleItemsCount));
	}

}
