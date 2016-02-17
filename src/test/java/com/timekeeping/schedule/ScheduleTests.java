package com.timekeeping.schedule;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class ScheduleTests {

	@Test
	public void updateWithEmpty() {
		ScheduleItemBuilder itemBuilder = new ScheduleItemBuilder();
		ScheduleBuilder scheduleBuilder  = new ScheduleBuilder();
		Schedule schedule = scheduleBuilder
				.withItem(itemBuilder.employeeId(1).build())
				.withItem(itemBuilder.employeeId(2).build())
				.withItem(itemBuilder.employeeId(3).build())
				.build();
		
		schedule.updateItems(new HashSet<ScheduleItem>());
		
		assertThat(schedule.getItems().size(), equalTo(3));
	}
	
	@Test
	public void updateWithAllNew() {
		ScheduleItemBuilder itemBuilder = new ScheduleItemBuilder();
		ScheduleBuilder scheduleBuilder  = new ScheduleBuilder();
		Schedule schedule = scheduleBuilder
				.withItem(itemBuilder.employeeId(1).build())
				.withItem(itemBuilder.employeeId(2).build())
				.withItem(itemBuilder.employeeId(3).build())
				.build();
		
		schedule.updateItems(Arrays.asList(
					itemBuilder.employeeId(4).build(),
					itemBuilder.employeeId(5).build(),
					itemBuilder.employeeId(6).build()
				));
		
		assertThat(schedule.getItems().size(), equalTo(6));
	}
	
	@Test
	public void updateWithSomeNew() {
		ScheduleItemBuilder itemBuilder = new ScheduleItemBuilder();
		ScheduleBuilder scheduleBuilder  = new ScheduleBuilder();
		Schedule schedule = scheduleBuilder
				.withItem(itemBuilder.employeeId(1).build())
				.withItem(itemBuilder.employeeId(2).build())
				.withItem(itemBuilder.employeeId(3).build())
				.build();
		
		schedule.updateItems(Arrays.asList(
				itemBuilder.employeeId(2).build(),
				itemBuilder.employeeId(3).build(),
				itemBuilder.employeeId(4).build()
				));
		
		assertThat(schedule.getItems().size(), equalTo(4));
	}
	
	@Test
	public void updateWithNothingNew() {
		ScheduleItemBuilder itemBuilder = new ScheduleItemBuilder();
		ScheduleBuilder scheduleBuilder  = new ScheduleBuilder();
		Schedule schedule = scheduleBuilder
				.withItem(itemBuilder.employeeId(1).build())
				.withItem(itemBuilder.employeeId(2).build())
				.withItem(itemBuilder.employeeId(3).build())
				.build();
		
		schedule.updateItems(Arrays.asList(
				itemBuilder.employeeId(1).build(),
				itemBuilder.employeeId(2).build(),
				itemBuilder.employeeId(3).build()
				));
		
		assertThat(schedule.getItems().size(), equalTo(3));
	}

}
