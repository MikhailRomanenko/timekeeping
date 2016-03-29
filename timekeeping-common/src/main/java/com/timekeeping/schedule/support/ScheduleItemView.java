package com.timekeeping.schedule.support;

import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;

/**
 * Value object designed to represent a {@link ScheduleItem} at the view layer.
 * @author Mikhail Romanenko
 *
 */
public final class ScheduleItemView {

	private final Long employeeId;
	private final int startTime;
	private final int duration;
	private final WorkType type;
	
	public static ScheduleItemView of(ScheduleItem item) {
		return new ScheduleItemView(item);
	}
	
	private ScheduleItemView(ScheduleItem item) {
		this.employeeId = item.getEmployee().getId();
		this.startTime = item.getStartTime();
		this.duration = item.getDuration();
		this.type = item.getType();
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getDuration() {
		return duration;
	}

	public WorkType getType() {
		return type;
	}
	
}
