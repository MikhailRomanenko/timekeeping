package com.timekeeping.schedule;

import java.io.Serializable;
import java.util.Objects;

/**
 * JPA compound key for supporting {@link ScheduleItem}.
 * 
 * @author Mikhail Romanenko
 *
 */
@SuppressWarnings("serial")
public class ScheduleItemId implements Serializable {
	private Long schedule;
	private Long employee;
	
	ScheduleItemId(){
		
	}
	
	public ScheduleItemId(Long schedule, Long employee) {
		this.schedule = schedule;
		this.employee = employee;
	}

	public Long getSchedule() {
		return schedule;
	}

	public Long getEmployee() {
		return employee;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.schedule, this.employee);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleItemId other = (ScheduleItemId) obj;
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
