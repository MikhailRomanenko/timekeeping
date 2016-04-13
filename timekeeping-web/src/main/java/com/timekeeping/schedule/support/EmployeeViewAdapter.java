package com.timekeeping.schedule.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.timekeeping.employee.Employee;

/**
 * Service provides functionality for converting {@link Employee} entities for the presentation layer.
 * @author Mikhail Romanenko
 *
 */
@Service
public class EmployeeViewAdapter {

	/**
	 * Convert list of employees to {@code Map} representation.
	 * Employee's id is used as a key, and the employee as a value.
	 * @param employees list to convert to the map
	 * @return map of the employees
	 */
	public Map<Long, Employee> toMapView(List<Employee> employees) {
		if(employees == null) return Collections.emptyMap();
		return employees.stream()
				.collect(Collectors.toMap(Employee::getId, Function.identity()));
	}
	
}
