package com.timekeeping.schedule.support;

import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.WorkType;

/**
 * Value object designed to represent a {@link ScheduleItem} at the view layer.
 * @author Mikhail Romanenko
 *
 */
public final class ScheduleItemView {

	private Long employeeId;
	private int startTime;
	private int duration;
	private WorkType type;
	
	public static ScheduleItemView of(ScheduleItem item) {
		return new ScheduleItemView(item);
	}
	
	ScheduleItemView() {
		
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
