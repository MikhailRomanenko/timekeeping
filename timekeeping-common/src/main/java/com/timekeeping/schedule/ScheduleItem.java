package com.timekeeping.schedule;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.timekeeping.employee.Employee;

/**
 * JPA entity representing a particular employee's work time in {@link Schedule}'s items.
 * 
 * Start and duration time supplied as {@code int} values. Start time represents value in minutes 
 * from midnight to the beginning of work. Duration time also represents value in minutes 
 * that employee should work. 
 * 
 * @author Mikhail Romanenko
 *
 */

@Entity
@IdClass(ScheduleItemId.class)
public class ScheduleItem {
	@JsonBackReference
	@Id
	@ManyToOne
	@JoinColumn(name = "SCHEDULE_ID")
	private Schedule schedule;
	@Id
	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID")
	private Employee employee;
	private int startTime;
	private int duration;
	@Enumerated(EnumType.STRING)
	@Column(name = "WORK_TYPE")
	private WorkType type;
	
	protected ScheduleItem() {		
	}

	public ScheduleItem(Schedule schedule, Employee employee, int startTime, int duration, WorkType type) {
		this.schedule = schedule;
		this.employee = employee;
		this.startTime = startTime;
		this.duration = duration;
		this.type = type;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public WorkType getType() {
		return type;
	}

	public void setType(WorkType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScheduleItem [scheduleID: ").append(schedule != null ? schedule.getId() : "null")
				.append(", employeeID: ").append(employee != null ? employee.getId() : "null")
				.append(", startTime: ").append(startTime)
				.append(", duration: ").append(duration).append(", type: ")
				.append(type).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(schedule, employee);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleItem other = (ScheduleItem) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		return true;
	}
		
}
