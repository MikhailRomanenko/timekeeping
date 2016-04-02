package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;

/**
 * Value object designed to represent a {@link Schedule} at the view layer.
 * @author Mikhail Romanenko
 *
 */
public final class ScheduleView {
	
	private Long shopId;
	private LocalDate date;
	private int version;
	private Map<String, Map<Long, ScheduleItemView>> items = new HashMap<>();
	
	public static ScheduleView of(Schedule schedule) {
		return new ScheduleView(schedule);
	}
	
	ScheduleView() {
		
	}
	
	private ScheduleView(Schedule schedule) {
		this.shopId = schedule.getShop().getId();
		this.date = schedule.getDate();
		this.version = schedule.getVersion();
		convertToMap(schedule.getItems());
	}
	
	private void convertToMap(Collection<ScheduleItem> items) {
		items.stream()
			.collect(Collectors.groupingBy((ScheduleItem item) -> item.getEmployee().getPosition().getDepartment(),	() -> this.items,
												Collectors.toMap((item) -> item.getEmployee().getId(), (item) -> ScheduleItemView.of(item))));
	}

	public Long getShopId() {
		return shopId;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public int getVersion() {
		return version;
	}

	public Map<String, Map<Long, ScheduleItemView>> getItems() {
		return items;
	}

}
