package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.TimekeepingWebApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimekeepingWebApplication.class)
@ActiveProfiles("dev")
public class ScheduleServiceSecurityTest {

	@Autowired
	private ScheduleService scheduleService;
	
	@Test
	@WithUserDetails("user1")
	public void findOwnShop() {
		scheduleService.findSchedule(1L, LocalDate.of(2016, Month.MARCH, 1));
	}
	
	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void findShopWithAdmin() {
		scheduleService.findSchedule(1L, LocalDate.of(2016, Month.MARCH, 1));
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithUserDetails("user2")
	public void findForeignShop() {
		scheduleService.findSchedule(1L, LocalDate.of(2016, Month.MARCH, 1));
	}
	
	@Test
	@WithUserDetails("user1")
	@Transactional
	public void saveOwnSchedule() {
		ScheduleView view = ScheduleView.emptyFor(1L, LocalDate.of(2016, Month.MARCH, 10));
		scheduleService.saveSchedule(view);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithUserDetails("user2")
	public void saveForeignSchedule() {
		ScheduleView view = ScheduleView.emptyFor(1L, LocalDate.of(2016, Month.MARCH, 10));
		scheduleService.saveSchedule(view);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithUserDetails("user1")
	public void removeScheduleForAdminsOnly() {
		scheduleService.removeSchedule(1L, LocalDate.of(2016, Month.MARCH, 1));
	}
	
}
