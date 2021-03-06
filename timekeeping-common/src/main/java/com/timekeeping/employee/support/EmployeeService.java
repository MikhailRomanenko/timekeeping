package com.timekeeping.employee.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.employee.Employee;
import com.timekeeping.shop.Shop;

/**
 * Service providing high-level data access and other {@link Employee}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	/**
	 * Find a list of employees belonging to the {@link Shop} with specified
	 * {@code shopId} value.
	 * 
	 * @param shopId
	 *            id of the shop
	 * @return {@code List} of employees
	 */
	@PreAuthorize("hasRole('ADMIN') || hasPermission(#shopId, 'read')")
	public List<Employee> findByShopId(Long shopId) {
		return employeeRepository.findByShopId(shopId);
	}

}
