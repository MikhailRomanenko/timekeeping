package com.timekeeping.schedule;

import com.timekeeping.employee.Employee;

public class ScheduleItemBuilder {
	private Schedule schedule;
	private Employee employee;
	private int startTime;
	private int duration;
	private WorkType type;
	
	public ScheduleItem build() {
		return new ScheduleItem(schedule, employee, startTime, duration, type);
	}
	
	public ScheduleItemBuilder employeeId(final long employeeId) {
		this.employee = new Employee() {
			@Override
			public Long getId() {
				return employeeId;
			}
		};
		return this;
	}
	
	public ScheduleItemBuilder schedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public ScheduleItemBuilder employee(Employee employee) {
		this.employee = employee;
		return this;
	}
	
	public ScheduleItemBuilder startTime(int startTime) {
		this.startTime = startTime;
		return this;
	}
	
	public ScheduleItemBuilder duration(int duration) {
		this.duration = duration;
		return this;
	}
	
	public ScheduleItemBuilder type(WorkType type) {
		this.type = type;
		return this;
	}
}
