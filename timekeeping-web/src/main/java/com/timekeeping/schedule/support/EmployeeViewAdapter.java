package com.timekeeping.schedule.support;

import com.timekeeping.employee.Employee;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service provides functionality for converting {@link Employee} entities for the presentation layer.
 * @author Mikhail Romanenko
 *
 */
@Service
public class EmployeeViewAdapter {

	/**
	 * Convert list of employees to {@code List} containing of values pairs.
	 * Each pair is a {@code Map} which contains department name and
	 * list of employees belonging to the department.
	 * @param employees list containing all shop's employees
	 * @return list containing department name / department employees values
     */
	public List<?> adapt(List<Employee> employees) {
		Set<Map.Entry<String, List<Employee>>> entries = employees.stream()
				.collect(Collectors.groupingBy(employee -> employee.getPosition().getDepartment()))
				.entrySet();
		return entries.stream()
				.collect(Collectors.mapping(entry -> {
					Map<String, Object> map = new HashMap<>();
					map.put("department", entry.getKey());
					map.put("employees", entry.getValue());
					return map;
				}, Collectors.toList()));
	}
	
}
