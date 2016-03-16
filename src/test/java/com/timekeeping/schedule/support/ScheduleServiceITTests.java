package com.timekeeping.schedule.support;

import static com.timekeeping.employee.EmployeeBuilder.employee;
import static com.timekeeping.schedule.ScheduleBuilder.schedule;
import static com.timekeeping.schedule.ScheduleItemBuilder.item;
import static com.timekeeping.shop.ShopBuilder.shop;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.RepositoryTestConfig;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.shop.support.ShopRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RepositoryTestConfig.class)
@TestPropertySource(locations = "classpath:\\test.properties")
public class ScheduleServiceITTests {
	
	@Autowired
	private EntityManager em;

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	@Autowired
	private ShopRepository shopRepository;
	private ScheduleService service;
	private BreakPolicy breakPolicy;
	
	private static Matcher<ScheduleItem> hasStartTime(final int startTime) {
		return new TypeSafeDiagnosingMatcher<ScheduleItem>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("startTime should be equal to ").appendValue(startTime);
			}

			@Override
			protected boolean matchesSafely(ScheduleItem item, Description mismatchDescription) {
				mismatchDescription.appendText(" was ").appendValue(item.getStartTime());
				return item.getStartTime() == startTime;
			}
			
		};
	}
	
	private static Matcher<ScheduleItem> hasDuration(final int duration) {
		return new TypeSafeDiagnosingMatcher<ScheduleItem>() {
			
			@Override
			public void describeTo(Description description) {
				description.appendText("duration should be equal to ").appendValue(duration);
			}
			
			@Override
			protected boolean matchesSafely(ScheduleItem item, Description mismatchDescription) {
				mismatchDescription.appendText(" was ").appendValue(item.getDuration());
				return item.getDuration() == duration;
			}
			
		};
	}
	
	private static Matcher<ScheduleItem> hasEmployeeWithId(final long id) {
		return new TypeSafeDiagnosingMatcher<ScheduleItem>() {
			
			@Override
			public void describeTo(Description description) {
				description.appendText("employee.ID should be equal to ").appendValue(id);
			}
			
			@Override
			protected boolean matchesSafely(ScheduleItem item, Description mismatchDescription) {
				mismatchDescription.appendText(" was ").appendValue(item.getEmployee().getId());
				return item.getEmployee().getId() == id;
			}
			
		};
	}
	
	@Before
	public void setUp() {
		breakPolicy = mock(BreakPolicy.class);
		given(breakPolicy.mapper()).willReturn(v -> v);
		service = new ScheduleService(scheduleRepository, scheduleItemRepository,
				shopRepository,  breakPolicy);
	}
	
	@Test
	public void shouldCalculateStatisticTest() {
		WorkStatistic statistic = service.getStatistic(LocalDate.of(2016, 3, 1), 2L);
		assertThat(statistic.getTimeWorked(), equalTo(780));
		assertThat(statistic.getTimeForWeek(), equalTo(2340));
		assertThat(statistic.getTimeForMonth(), equalTo(1620));
	}
	
	@Test
	@Transactional
	public void saveNonExistingScheduleShouldCreateNewScheduleTest() {
		long totalItemsCount = scheduleItemRepository.count();
		long totalSchedulesCount = scheduleRepository.count();
		LocalDate date = LocalDate.of(2016, 3, 8);
		Long shopId = 1L;
	
		Set<ScheduleItem> items = new HashSet<>();
		items.add(item().employee(employee().id(1L).build()).build());
		items.add(item().employee(employee().id(2L).build()).build());
		items.add(item().employee(employee().id(3L).build()).build());
		Schedule schedule = schedule()
				.date(date).shop(shop().id(shopId).build())
				.items(items).build();
	
		service.saveSchedule(schedule);
		em.flush();
		
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount + items.size()));
		assertThat(scheduleRepository.count(), equalTo(totalSchedulesCount + 1));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	public void saveExistingScheduleShouldUpdateItemsTest() {
		long totalItemsCount = scheduleItemRepository.count();
		long totalSchedulesCount = scheduleRepository.count();
		long scheduleId = 1L;
		Schedule existingSchedule = scheduleRepository.findOne(scheduleId);
		
		Set<ScheduleItem> items = new HashSet<>();
		items.add(item().employee(employee().id(2L).build()).build());
		items.add(item().employee(employee().id(4L).build()).build());
		
		Schedule schedule = schedule()
				.date(existingSchedule.getDate()).shop(existingSchedule.getShop())
				.items(items).build();
		service.saveSchedule(schedule);
		em.flush();
		
		Schedule updatedSchedule = scheduleRepository.findOne(scheduleId);
		assertThat(updatedSchedule.getItems(), containsInAnyOrder(hasEmployeeWithId(2L), hasEmployeeWithId(4L)));
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount - 1));
		assertThat(scheduleRepository.count(), equalTo(totalSchedulesCount));
	}
	
	@Test
	@Transactional
	public void saveExistingScheduleShouldUpdateTimeValuesItemsTest() {
		long totalItemsCount = scheduleItemRepository.count();
		long totalSchedulesCount = scheduleRepository.count();
		long scheduleId = 1L;
		final int startTime = 100;
		final int duration = 200;
		Schedule existingSchedule = scheduleRepository.findOne(scheduleId);
	
		Set<ScheduleItem> items = new HashSet<>();
		items.add(item().employee(employee().id(1L).build()).startTime(startTime).duration(duration).build());
		items.add(item().employee(employee().id(2L).build()).startTime(startTime).duration(duration).build());
		items.add(item().employee(employee().id(3L).build()).startTime(startTime).duration(duration).build());
	
		Schedule schedule = schedule()
				.date(existingSchedule.getDate()).shop(existingSchedule.getShop())
				.items(items).build();
		service.saveSchedule(schedule);
		em.flush();
		
		Schedule updatedSchedule = scheduleRepository.findOne(scheduleId);
		assertThat(updatedSchedule.getItems(), everyItem(allOf(hasStartTime(startTime), hasDuration(duration))));
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount));
		assertThat(scheduleRepository.count(), equalTo(totalSchedulesCount));
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Transactional
	public void saveWithWrongShopIdShouldRaiseExceptionTest() {
		long shopId = 10L;
		LocalDate date = LocalDate.of(2016, 3, 1);
		Set<ScheduleItem> items = new HashSet<>();
		Schedule schedule = schedule()
				.date(date).shop(shop().id(shopId).build())
				.items(items).build();
		service.saveSchedule(schedule);
	}
	
	@Test(expected = OptimisticLockingFailureException.class)
	@Transactional
	public void concurrentSaveShouldRaiseExceptionTest() {
		long shopId = 1L;
		LocalDate date = LocalDate.of(2016, 3, 1);
		Set<ScheduleItem> items = new HashSet<>();
		Schedule schedule = schedule()
				.date(date).shop(shop().id(shopId).build())
				.items(items).version(0).id(2L).build();
		service.saveSchedule(schedule);
	}
}
