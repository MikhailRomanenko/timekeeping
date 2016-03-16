package com.timekeeping.schedule;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.test.util.ReflectionTestUtils;

import com.timekeeping.shop.Shop;

public class ScheduleBuilder {
	private Long id = 1L;
	private LocalDate date = LocalDate.of(2016, 3, 1);
	private Shop shop;
	private int version;
	private Set<ScheduleItem> items = new HashSet<>();
	
	private ScheduleBuilder() {
	}
	
	public static ScheduleBuilder schedule() {
		return new ScheduleBuilder();
	}
	
	public Schedule build() {
		Schedule schedule = new Schedule(shop, date);
		ReflectionTestUtils.setField(schedule, "id", id);
		ReflectionTestUtils.setField(schedule, "version", version);
		ReflectionTestUtils.setField(schedule, "items", items);
		return schedule;
	}
	public ScheduleBuilder withItem(ScheduleItem item) {
		items.add(item);
		return this;
	}

	public ScheduleBuilder id(Long id) {
		this.id = id;
		return this;
	}
	
	public ScheduleBuilder date(LocalDate date) {
		this.date = date;
		return this;
	}
	
	public ScheduleBuilder shop(Shop shop) {
		this.shop = shop;
		return this;
	}
	
	public ScheduleBuilder version(int version) {
		this.version = version;
		return this;
	}
	
	public ScheduleBuilder items(Set<ScheduleItem> items) {
		this.items = items;
		return this;
	}

}
