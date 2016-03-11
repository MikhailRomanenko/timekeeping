package com.timekeeping.schedule;

import static com.timekeeping.employee.EmployeeBuilder.employee;
import static com.timekeeping.schedule.ScheduleBuilder.schedule;
import static com.timekeeping.schedule.ScheduleItemBuilder.item;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

public class ScheduleTests {
	
	@SuppressWarnings("unchecked")
	private <T> void assertItems(Set<ScheduleItem> items, T... expected) {
		assertThat(items, hasSize(expected.length));
		assertThat(items.stream().map(i -> i.getEmployee().getId()).collect(Collectors.toList()),
				contains(expected));
	}

	@Test
	public void updateWithEmpty() {
		Schedule schedule = schedule()
				.withItem(item().employee(employee().id(1L).build()).build())
				.withItem(item().employee(employee().id(2L).build()).build())
				.withItem(item().employee(employee().id(3L).build()).build())
				.build();
		
		schedule.updateItems(new HashSet<ScheduleItem>());

		assertThat(schedule.getItems(), hasSize(0));
	}
	
	@Test
	public void updateWithAllNew() {
		Schedule schedule = schedule()
				.withItem(item().employee(employee().id(1L).build()).build())
				.withItem(item().employee(employee().id(2L).build()).build())
				.withItem(item().employee(employee().id(3L).build()).build())
				.build();
		
		
		schedule.updateItems(Arrays.asList(
					item().employee(employee().id(4L).build()).build(),
					item().employee(employee().id(5L).build()).build(),
					item().employee(employee().id(6L).build()).build()
				));
		
		assertItems(schedule.getItems(), 4L, 5L, 6L);
	}
	
	@Test
	public void updateWithSomeNew() {
		Schedule schedule = schedule()
				.withItem(item().employee(employee().id(1L).build()).build())
				.withItem(item().employee(employee().id(2L).build()).build())
				.withItem(item().employee(employee().id(3L).build()).build())
				.build();
		
		schedule.updateItems(Arrays.asList(
				item().employee(employee().id(2L).build()).build(),
				item().employee(employee().id(4L).build()).build()
			));
		
		assertItems(schedule.getItems(), 2L, 4L);
	}
	
	@Test
	public void updateWithNothingNew() {
		Schedule schedule = schedule()
				.withItem(item().employee(employee().id(1L).build()).build())
				.withItem(item().employee(employee().id(2L).build()).build())
				.withItem(item().employee(employee().id(3L).build()).build())
				.build();
		
		schedule.updateItems(Arrays.asList(
				item().employee(employee().id(1L).build()).build(),
				item().employee(employee().id(2L).build()).build(),
				item().employee(employee().id(3L).build()).build()
			));
		
		assertItems(schedule.getItems(), 1L, 2L, 3L);
	}

}
