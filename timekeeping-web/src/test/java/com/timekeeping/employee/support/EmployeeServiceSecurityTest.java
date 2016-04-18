package com.timekeeping.employee.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.timekeeping.TimekeepingWebApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimekeepingWebApplication.class)
@ActiveProfiles("dev")
public class EmployeeServiceSecurityTest {

	@Autowired
	private EmployeeService employeeService;
	
	@Test
	@WithUserDetails("user1")
	public void accessOwnShop() {
		employeeService.findByShopId(1L);
	}
	
	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void accessShopWithAdmin() {
		employeeService.findByShopId(1L);
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithUserDetails("user2")
	public void failForeignShop() {
		employeeService.findByShopId(1L);
	}
	
}
