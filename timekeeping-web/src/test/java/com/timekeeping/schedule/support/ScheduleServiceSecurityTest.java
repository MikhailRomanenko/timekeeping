package com.timekeeping.schedule.support;

import com.timekeeping.TimekeepingWebApplication;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopService;
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

import java.time.LocalDate;
import java.time.Month;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimekeepingWebApplication.class)
@ActiveProfiles("dev")
public class ScheduleServiceSecurityTest {

	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private ShopService shopService;
	
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
		Shop shop = shopService.find(1L);
		Schedule schedule = new Schedule(shop, LocalDate.of(2016, Month.MARCH, 10));
		scheduleService.saveSchedule(schedule);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithUserDetails("user2")
	public void saveForeignSchedule() {
		Shop shop = shopService.find(1L);
		Schedule schedule = new Schedule(shop, LocalDate.of(2016, Month.MARCH, 10));
		scheduleService.saveSchedule(schedule);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithUserDetails("user1")
	public void removeScheduleForAdminsOnly() {
		scheduleService.removeSchedule(1L, LocalDate.of(2016, Month.MARCH, 1));
	}
	
}
