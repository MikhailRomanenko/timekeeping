package com.timekeeping.schedule.support;

import static com.timekeeping.employee.EmployeeBuilder.employee;
import static com.timekeeping.schedule.ScheduleBuilder.schedule;
import static com.timekeeping.schedule.ScheduleItemBuilder.item;
import static com.timekeeping.shop.ShopBuilder.shop;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.Test;

import com.timekeeping.employee.Position;
import com.timekeeping.schedule.Schedule;

public class ScheduleViewTest {
	
	private static final LocalDate DATE = LocalDate.of(2016, 3, 1);
	private static final Long SHOP_ID = 1L;
	
	@SuppressWarnings("unchecked")
	@Test
	public void createViewFromScheduleTest() {
		Schedule schedule = schedule().date(DATE).shop(shop().id(SHOP_ID).build())
				.withItem(item().employee(employee().id(1L).position(new Position("Manager", "Management")).build()).build())
				.withItem(item().employee(employee().id(5L).position(new Position("Admin", "Management")).build()).build())
				.withItem(item().employee(employee().id(3L).position(new Position("Saler", "Sales")).build()).build())
				.withItem(item().employee(employee().id(7L).position(new Position("Sr. Saler", "Sales")).build()).build())
				.withItem(item().employee(employee().id(9L).position(new Position("Jr. Saler", "Sales")).build()).build())
				.withItem(item().employee(employee().id(4L).position(new Position("Stockmen", "Warehouse")).build()).build())
				.build();
		
		ScheduleView view = ScheduleView.of(schedule);

		assertThat(view.getShopId(), equalTo(SHOP_ID));
		assertThat(view.getDate(), equalTo(DATE));
		assertThat(view.getItems().keySet(), hasSize(3));
		assertThat(view.getItems().keySet(), containsInAnyOrder("Management", "Sales", "Warehouse"));
		
		assertThat(view.getItems().get("Management").keySet(), containsInAnyOrder(equalTo(5L), equalTo(1L)));
		assertThat(view.getItems().get("Sales").keySet(), containsInAnyOrder(equalTo(7L), equalTo(3L), equalTo(9L)));
		assertThat(view.getItems().get("Warehouse").keySet(), contains(equalTo(4L)));
	}

}
