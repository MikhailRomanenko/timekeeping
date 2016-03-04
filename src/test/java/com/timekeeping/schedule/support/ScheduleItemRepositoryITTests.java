package com.timekeeping.schedule.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.timekeeping.RepositoryTestConfig;
import com.timekeeping.employee.Employee;
import com.timekeeping.employee.support.EmployeeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RepositoryTestConfig.class)
@TestPropertySource(locations = "classpath:\\test.properties")
public class ScheduleItemRepositoryITTests {

	@Autowired
	private ScheduleItemRepository scheduleItemRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	private static final LocalDate from = LocalDate.of(2016, 2, 29);
	private static final LocalDate to = LocalDate.of(2016, 3, 2);

	@Test
	public void findWorkingTimeTest() {
		Employee employee = employeeRepository.findOne(1L);
		List<Integer> times = scheduleItemRepository
				.findWorkingTimeByEmployeeAndDateRange(employee, from, to)
				.stream().map(t -> t.getDuration()).collect(Collectors.toList());
		assertThat(times, hasSize(3));
		assertThat(times, everyItem(equalTo(240)));
	}

}