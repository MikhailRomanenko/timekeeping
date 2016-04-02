package com.timekeeping.schedule.support;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;
import com.timekeeping.employee.Employee;
import com.timekeeping.employee.support.EmployeeService;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopService;
import com.timekeeping.support.JView;
import com.timekeeping.user.User;

@Controller
public class ScheduleController {
	
	private final static String SCHEDULE_VIEW = "schedule/index";
	
	private final ScheduleService scheduleService;
	private final EmployeeService employeeService;
	private final ShopService shopService;
	
	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService, ShopService shopService) {
		this.scheduleService = scheduleService;
		this.employeeService = employeeService;
		this.shopService = shopService;
	}
	
	@RequestMapping(value = "schedule", method = RequestMethod.GET)
	public String show(@AuthenticationPrincipal User user, Model model) {
		Assert.notNull(user);
		List<Shop> shops = shopService.findByUserLogin(user.getLogin());
		model.addAttribute("shops", shops);
		return SCHEDULE_VIEW;
	}
	
	@RequestMapping(value = "api/employee/{shopId}", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(JView.TimeTable.class)
	public List<Employee> getEmployees(@PathVariable Long shopId) {
		return employeeService.findByShopId(shopId);
	}
	
	@RequestMapping(value = "api/schedule/{shopId:[0-9]+}/{date}", method = RequestMethod.GET)
	@ResponseBody
	public ScheduleView getSchedule(@PathVariable Long shopId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		Schedule schedule = scheduleService.findSchedule(shopId, date);
		if(schedule == null) {
			return ScheduleView.emptyFor(shopId, date);
		} else {
			return ScheduleView.of(schedule);
		}
	}
	
	@RequestMapping(value = "api/schedule", method = RequestMethod.POST)
	@ResponseBody
	public void saveSchedule(@RequestBody ScheduleView scheduleView) {
		scheduleService.saveSchedule(scheduleView);
	}
	
}