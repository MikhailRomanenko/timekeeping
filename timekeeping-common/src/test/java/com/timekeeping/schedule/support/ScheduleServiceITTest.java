package com.timekeeping.schedule.support;

import com.timekeeping.AbstractRepositoryIntegerationTest;
import com.timekeeping.employee.support.EmployeeRepository;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleBuilder;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.timekeeping.schedule.ScheduleBuilder.schedule;
import static com.timekeeping.shop.ShopBuilder.shop;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleServiceITTest extends AbstractRepositoryIntegerationTest {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ScheduleService scheduleService;

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

	@Test
	@Transactional
	public void saveNonExistingScheduleShouldCreateNewScheduleTest() {
		long totalItemsCount = scheduleItemRepository.count();
		long totalSchedulesCount = scheduleRepository.count();
		LocalDate date = LocalDate.of(2016, 3, 8);
		Long shopId = 1L;
		Shop shop = shopService.find(shopId);
		Schedule schedule = new Schedule(shop, date);
		Set<ScheduleItem> items = new HashSet<>();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(2L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(3L), 100, 200, WorkType.WORK));
		schedule.setItems(items);
	
		Schedule actual = scheduleService.saveSchedule(schedule);
		entityManager.flush();

		assertThat(actual.getId(), notNullValue());
		assertThat(actual.getItems(), hasSize(3));
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
		Schedule schedule = scheduleRepository.findOne(scheduleId);
		int prevVersion = schedule.getVersion();
		Set<ScheduleItem> items = new HashSet<>();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 240, 600, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(2L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(4L), 100, 200, WorkType.WORK));
		schedule.setItems(items);
		
		Schedule actual = scheduleService.saveSchedule(schedule);
		entityManager.flush();

		assertThat(actual.getVersion(), equalTo(prevVersion + 1));
		assertThat(actual.getItems(), hasSize(3));
		assertThat(actual.getItems(), containsInAnyOrder(hasEmployeeWithId(1L),
				hasEmployeeWithId(2L), hasEmployeeWithId(4L)));
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount));
		assertThat(scheduleRepository.count(), equalTo(totalSchedulesCount));
	}

	@Test
	@Transactional
	public void saveNotExistingScheduleWithSpecifiedIdShouldPersistNewSchedule() {
		long totalItemsCount = scheduleItemRepository.count();
		long totalSchedulesCount = scheduleRepository.count();
		LocalDate date = LocalDate.of(2016, 3, 8);
		Long shopId = 1L;

		Shop shop = shopService.find(shopId);
		Set<ScheduleItem> items = new HashSet<>();
		Schedule schedule = ScheduleBuilder.schedule().date(date).shop(shop).id(10L).items(items).build();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(2L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(3L), 100, 200, WorkType.WORK));
		schedule.setItems(items);

		Schedule actual = scheduleService.saveSchedule(schedule);
		entityManager.flush();

		assertThat(actual.getVersion(), equalTo(0));
		assertThat(actual.getId(), equalTo(4L));
		assertThat(scheduleItemRepository.count(), equalTo(totalItemsCount + items.size()));
		assertThat(scheduleRepository.count(), equalTo(totalSchedulesCount + 1));
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
		Schedule newSchedule = scheduleService.saveSchedule(schedule);
		System.out.println(newSchedule.getVersion());
	}
}
