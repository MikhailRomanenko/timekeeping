package com.timekeeping.schedule;

import static com.timekeeping.employee.EmployeeBuilder.employee;
import static com.timekeeping.schedule.ScheduleBuilder.schedule;
import static com.timekeeping.schedule.ScheduleItemBuilder.item;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class ScheduleTests {

	@Test
	public void updateWithEmpty() {
		Schedule schedule = schedule()
				.withItem(item().employee(employee().id(1L).build()).build())
				.withItem(item().employee(employee().id(2L).build()).build())
				.withItem(item().employee(employee().id(3L).build()).build())
				.build();
		
		schedule.updateItems(new HashSet<ScheduleItem>());
		
		assertThat(schedule.getItems(), hasSize(3));
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
		
		assertThat(schedule.getItems(), hasSize(6));
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
				item().employee(employee().id(3L).build()).build(),
				item().employee(employee().id(4L).build()).build()
			));
		
		assertThat(schedule.getItems(), hasSize(4));
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
		
		assertThat(schedule.getItems(), hasSize(3));
	}

}
