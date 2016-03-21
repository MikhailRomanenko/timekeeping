package com.timekeeping.employee.support;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timekeeping.employee.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	List<Employee> findByShopId(Long shopId);
	
}
