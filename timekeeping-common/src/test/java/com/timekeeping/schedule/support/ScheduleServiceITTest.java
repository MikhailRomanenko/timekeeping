package com.timekeeping.schedule.support;

import com.timekeeping.AbstractRepositoryIntegrationTest;
import com.timekeeping.employee.support.EmployeeRepository;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleBuilder;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopRepository;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleServiceITTest extends AbstractRepositoryIntegrationTest {

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ShopRepository shopRepository;
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
				return item.getEmployee().getId().equals(id);
			}
			
		};
	}

	@Test
	@Sql(scripts = "/schedule/contains-no-schedule-data.sql")
	@Sql(scripts = "/schedule/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void saveNonExistingScheduleShouldCreateNewScheduleTest() {
		assertThat(scheduleItemRepository.count(), equalTo(0L));
		assertThat(scheduleRepository.count(), equalTo(0L));

		LocalDate date = LocalDate.of(2016, 6, 1);
		Shop shop = shopRepository.findOne(1L);
		Schedule schedule = new Schedule(shop, date);
		Set<ScheduleItem> items = new HashSet<>();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(2L), 100, 200, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(3L), 100, 200, WorkType.WORK));
		schedule.setItems(items);
	
		Schedule actual = scheduleService.saveSchedule(schedule);

		assertThat(actual.getId(), notNullValue());
		assertThat(actual.getItems(), hasSize(3));

		assertThat(scheduleItemRepository.count(), equalTo((long)items.size()));
		assertThat(scheduleRepository.count(), equalTo(1L));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Sql(scripts = "/schedule/contains-one-schedule-data.sql")
	@Sql(scripts = "/schedule/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void saveExistingScheduleShouldUpdateItemsTest() {
		assertThat(scheduleItemRepository.count(), equalTo(3L));
		assertThat(scheduleRepository.count(), equalTo(1L));

		Schedule schedule = scheduleRepository.findOne(1L);
		Set<ScheduleItem> items = new HashSet<>();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 300, 400, WorkType.WORK));
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(3L), 300, 400, WorkType.WORK));
		schedule.setItems(items);

		Schedule actual = scheduleService.saveSchedule(schedule);

		assertThat(actual.getVersion(), equalTo(2));
		assertThat(actual.getItems(), hasSize(2));
		assertThat(actual.getItems(),
				containsInAnyOrder(hasEmployeeWithId(1L), hasEmployeeWithId(3L)));

		assertThat(scheduleItemRepository.count(), equalTo(2L));
		assertThat(scheduleRepository.count(), equalTo(1L));
	}

	@Test
	@Sql(scripts = "/schedule/contains-no-schedule-data.sql")
	@Sql(scripts = "/schedule/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void saveNotExistingScheduleWithSpecifiedIdShouldPersistNewSchedule() {
		assertThat(scheduleItemRepository.count(), equalTo(0L));
		assertThat(scheduleRepository.count(), equalTo(0L));

		LocalDate date = LocalDate.of(2016, 6, 1);
		Shop shop = shopRepository.findOne(1L);
		Set<ScheduleItem> items = new HashSet<>();
		Schedule schedule = ScheduleBuilder.schedule()
				.date(date).shop(shop).id(10L).items(items).build();
		items.add(new ScheduleItem(schedule, employeeRepository.findOne(1L), 100, 200, WorkType.WORK));
		schedule.setItems(items);

		Schedule actual = scheduleService.saveSchedule(schedule);

		assertThat(actual.getVersion(), equalTo(0));
		assertThat(actual.getId(), notNullValue());

		assertThat(scheduleItemRepository.count(), equalTo((long)items.size()));
		assertThat(scheduleRepository.count(), equalTo(1L));
	}
	
	@Test(expected = OptimisticLockingFailureException.class)
	@Sql(scripts = "/schedule/contains-one-schedule-data.sql")
	@Sql(scripts = "/schedule/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void concurrentSaveShouldRaiseExceptionTest() {
		Schedule first = scheduleRepository.findOne(1L);
		Schedule second = scheduleRepository.findOne(1L);

		second.removeItemIf(item -> item.getEmployee().getId().equals(1L));
		scheduleService.saveSchedule(second);

		first.removeItemIf(item -> item.getEmployee().getId().equals(2L));
		scheduleService.saveSchedule(first);
	}
}
