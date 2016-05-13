package com.timekeeping.schedule.support;

import com.fasterxml.jackson.annotation.JsonView;
import com.timekeeping.employee.support.EmployeeService;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.WorkType;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopService;
import com.timekeeping.support.JView;
import com.timekeeping.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller is in charge of user interactions and operations on schedule objects.
 * @author Mikhail Romanenko
 *
 */
@Controller
public class ScheduleController {
	
	private final static String SCHEDULE_VIEW = "schedule/index";
	
	private final ScheduleService scheduleService;
	private final EmployeeService employeeService;
	private final ShopService shopService;
	private final EmployeeViewAdapter employeeViewAdapter;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService,
			ShopService shopService, EmployeeViewAdapter employeeViewAdapter) {
		this.scheduleService = scheduleService;
		this.employeeService = employeeService;
		this.shopService = shopService;
		this.employeeViewAdapter = employeeViewAdapter;
	}
	
	@RequestMapping(value = "schedule", method = RequestMethod.GET)
	public String show(@AuthenticationPrincipal User user, Model model) {
		Assert.notNull(user);
		List<Shop> shops = shopService.findByUserLogin(user.getLogin());
		model.addAttribute("shops", shops);
		model.addAttribute("workTypes", StringUtils.arrayToDelimitedString(WorkType.values(), ","));
		return SCHEDULE_VIEW;
	}
	
	@RequestMapping(value = "api/shop/{shopId:[0-9]+}/employees", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(JView.TimeTable.class)
	public List<?> getEmployees(@PathVariable Long shopId) {
		return employeeViewAdapter.adapt(employeeService.findByShopId(shopId));
	}
	
	@RequestMapping(value = "api/schedule/{shopId:[0-9]+}/{date:\\d{4}-\\d{2}-\\d{2}}", method = RequestMethod.GET)
	@ResponseBody
	public Schedule getSchedule(@PathVariable Long shopId,
								@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		Schedule schedule = scheduleService.findSchedule(shopId, date);
		if(schedule != null)
			return schedule;
		else {
			Shop shop = shopService.find(shopId);
			Schedule empty = new Schedule(shop, date);
			return empty;
		}
	}
	
	@RequestMapping(value = "api/schedule", method = RequestMethod.POST)
	@ResponseBody
	public void saveSchedule(@RequestBody ScheduleView scheduleView) {
		scheduleService.saveSchedule(scheduleView);
	}
	
}
