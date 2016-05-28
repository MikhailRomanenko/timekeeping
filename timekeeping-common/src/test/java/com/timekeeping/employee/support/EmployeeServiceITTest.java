package com.timekeeping.employee.support;

import com.timekeeping.AbstractRepositoryIntegrationTest;
import com.timekeeping.employee.Employee;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class EmployeeServiceITTest extends AbstractRepositoryIntegrationTest {

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
