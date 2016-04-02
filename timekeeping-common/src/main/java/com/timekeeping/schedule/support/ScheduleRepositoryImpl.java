package com.timekeeping.schedule.support;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import com.timekeeping.schedule.Schedule;

/**
 * Implementation for the custom Spring Data based Schedule repository.
 * 
 * @author Mikhail Romanenko
 *
 */
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {
	
	private static final String COUNT_QUERY = "select count(s.id) from Schedule s where s.shop.id=:shopId and s.date=:date";
	
	private final EntityManager em;
	
	@Autowired
	public ScheduleRepositoryImpl(JpaContext jpaContext) {
		this.em = jpaContext.getEntityManagerByManagedType(Schedule.class);
	}

	/* (non-Javadoc)
	 * @see com.timekeeping.schedule.support.ScheduleRepositoryCustom
	 */
	@Override
	public boolean exists(Long shopId, LocalDate date) {
		Long count = 
				em.createQuery(COUNT_QUERY, Long.class)
				.setParameter("shopId", shopId)
				.setParameter("date", date)
				.getSingleResult();
		return count > 0;
	}

}
