package com.timekeeping.employee.support;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.timekeeping.AbstractRepositoryIntegerationTest;
import com.timekeeping.employee.Employee;

public class EmployeeServiceITTest extends AbstractRepositoryIntegerationTest {

	@Autowired
	private EmployeeRepository employeeRepository;
	private EmployeeService employeeService;
	
	@Before
	public void setUp() {
		employeeService = new EmployeeService(employeeRepository);
	}
	
	@Test
	public void shouldReturnFourEmployees() {
		List<Employee> result = employeeService.findByShopId(1L);
		
		assertThat(result, hasSize(4));
	}
	
	@Test
	public void shouldReturnTwoEmployees() {
		List<Employee> result = employeeService.findByShopId(2L);
		
		assertThat(result, hasSize(2));
	}
	
	@Test
	public void shouldReturnEmptyList() {
		List<Employee> result = employeeService.findByShopId(3L);
		
		assertThat(result, hasSize(0));
	}
	
}
