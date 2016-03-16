package com.timekeeping.schedule.support;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.employee.Employee;
import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.shop.Shop;

/**
 * Implementation for the custom Spring Data based Schedule repository.
 * 
 * @author Mikhail Romanenko
 *
 */
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {
	
	private static final String SELECT_QUERY = "select s from Schedule s where s.shop.id=:shopId and s.date=:date";
	
	private final EntityManager em;
	
	@Autowired
	public ScheduleRepositoryImpl(JpaContext jpaContext) {
		this.em = jpaContext.getEntityManagerByManagedType(Schedule.class);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdate(Schedule schedule) {
		Schedule scheduleToSave = null;
		try{
			scheduleToSave =
				em.createQuery(SELECT_QUERY, Schedule.class)
				.setParameter("shopId", schedule.getShop().getId())
				.setParameter("date", schedule.getDate())
				.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
				.getSingleResult();
		}
		catch(NoResultException e) {
			scheduleToSave = new Schedule(em.getReference(Shop.class, schedule.getShop().getId()), schedule.getDate());
		}
		
		Set<ScheduleItem> itemsToSave = attachEmployeeAndSchedule(schedule.getItems(), scheduleToSave);
		scheduleToSave.updateItems(itemsToSave);
		if(scheduleToSave.getId() == null)
			em.persist(scheduleToSave);
		else
			em.merge(scheduleToSave);
	}
	
	private Set<ScheduleItem> attachEmployeeAndSchedule(Collection<ScheduleItem> items, final Schedule schedule) {
		return items.stream().map(item -> {
			item.setEmployee(em.getReference(Employee.class, item.getEmployee().getId()));
			item.setSchedule(schedule);
			return item;
		}).collect(Collectors.toSet());
	}

}
