package com.timekeeping.schedule.support;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.timekeeping.employee.Employee;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.shop.Shop;

/**
 * Service responsible for convertion {@link ScheduleView} objects to dedicated
 * {@link Schedule} objects.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
public class ScheduleAdapter {

	private static final String SELECT_QUERY = "SELECT s FROM Schedule s WHERE s.shop.id=:shopId AND s.date=:date";

	private final EntityManager em;

	@Autowired
	public ScheduleAdapter(JpaContext jpaContext) {
		this.em = jpaContext.getEntityManagerByManagedType(Schedule.class);
	}

	/**
	 * Update schedule with provided {@code scheduleView}.
	 * 
	 * @param scheduleView
	 *            value object containg changes for schedule
	 * @return {@link Schedule} object populated with provided data
	 */
	@Transactional
	public Schedule updateSchedule(ScheduleView scheduleView) {
		Assert.notNull(scheduleView);
		Schedule schedule = em.createQuery(SELECT_QUERY, Schedule.class)
				.setParameter("shopId", scheduleView.getShopId()).setParameter("date", scheduleView.getDate())
				.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT).getSingleResult();
		Set<ScheduleItem> items = flattenToItemsSet(scheduleView.getItems(), schedule);
		schedule.updateItems(items);
		return schedule;
	}

	/**
	 * Create new {@link Schedule} corresponding provided {@code scheduleView}.
	 * 
	 * @param scheduleView
	 *            value object containg data for creating of the schedule
	 * @return {@link Schedule} object populated with provided data
	 */
	@Transactional
	public Schedule createSchedule(ScheduleView scheduleView) {
		Assert.notNull(scheduleView);
		Schedule schedule = new Schedule(em.getReference(Shop.class, scheduleView.getShopId()), scheduleView.getDate());
		Set<ScheduleItem> items = flattenToItemsSet(scheduleView.getItems(), schedule);
		schedule.setItems(items);
		return schedule;
	}

	private Set<ScheduleItem> flattenToItemsSet(Map<String, Map<Long, ScheduleItemView>> viewsMap,
			final Schedule schedule) {
		Assert.notNull(viewsMap);
		Assert.notNull(schedule);
		Set<ScheduleItemView> viewsSet = flatItemViews(viewsMap);
		Set<ScheduleItem> items = viewsSet.stream().map(view -> createScheduleItem(view, schedule))
				.collect(Collectors.toSet());
		return items;
	}

	private Set<ScheduleItemView> flatItemViews(Map<String, Map<Long, ScheduleItemView>> map) {
		Set<ScheduleItemView> views = map.values().stream().flatMap(m -> m.values().stream())
				.collect(Collectors.toSet());
		return views;
	}

	private ScheduleItem createScheduleItem(ScheduleItemView view, final Schedule schedule) {
		Employee employee = em.getReference(Employee.class, view.getEmployeeId());
		ScheduleItem item = new ScheduleItem(schedule, employee, view.getStartTime(), view.getDuration(),
				view.getType());
		return item;
	}

}
