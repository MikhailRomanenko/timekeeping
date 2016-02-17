package com.timekeeping.schedule;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.timekeeping.shop.Shop;

public class ScheduleBuilder {
	private Long id;
	private LocalDate date;
	private Shop shop;
	private int version;
	private Set<ScheduleItem> items;
	
	public ScheduleBuilder withItem(ScheduleItem item) {
		if(items == null)
			items = new HashSet<>();
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
		this.items = new HashSet<>(items);
		return this;
	}
	
	public Schedule build() {
		return new Schedule(id, date, shop, version, items);
	}
}
