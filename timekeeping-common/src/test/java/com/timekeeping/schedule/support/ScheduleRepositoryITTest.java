package com.timekeeping.schedule.support;

import com.timekeeping.AbstractRepositoryIntegrationTest;
import com.timekeeping.schedule.Schedule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleRepositoryITTest extends AbstractRepositoryIntegrationTest {
	private static final LocalDate DATE = LocalDate.of(2016, 3, 1);
	private static final long SHOP_ID = 1L;
	private static final long SCHEDULE_ID = 1L;

	@Autowired
	private EntityManager em;
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
		em.flush();
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
		em.flush();
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount - scheduleItemsCount));
	}

}
