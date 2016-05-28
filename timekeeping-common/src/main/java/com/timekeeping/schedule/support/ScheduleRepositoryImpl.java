package com.timekeeping.schedule.support;

import com.timekeeping.schedule.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Implementation of the custom Spring Data based Schedule repository.
 * 
 * @author Mikhail Romanenko
 *
 */
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {
	
	private static final String COUNT_QUERY =
			"select count(s.id) from Schedule s where s.shop.id=:shopId and s.date=:date";
	
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

	/* (non-Javadoc)
	 * @see com.timekeeping.schedule.support.ScheduleRepositoryCustom
	 */
	@Override
	@Transactional
	public Schedule saveOrUpdate(Schedule schedule) {
		Objects.requireNonNull(schedule, "Schedule should be not NULL!");
		Schedule persisted = null;
		if (Objects.nonNull(schedule.getId())) {
			persisted = em.find(Schedule.class, schedule.getId(),
					LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		}
		if(Objects.isNull(schedule.getId()) || Objects.isNull(persisted)) {
			persisted = schedule;
			persisted.nullId();
			persisted.getItems().forEach(item -> {
				item.setEmployee(em.merge(item.getEmployee()));
				em.persist(item);
			});
			em.persist(persisted);
		} else {
			if(!Objects.equals(schedule.getVersion(), persisted.getVersion()))
                throw new OptimisticLockException();
			persisted.removeItemIf(item -> {
                if(!schedule.getItems().contains(item)) {
                    em.remove(item);
                    return true;
                }
                return false;
            });
			Schedule finalPersisted = persisted;
			schedule.getItems().forEach(item -> {
                item.setEmployee(em.merge(item.getEmployee()));
                item.setSchedule(finalPersisted);
                if(finalPersisted.containsItem(item)) {
                    em.merge(item);
                } else {
                    finalPersisted.addItem(item);
                    em.persist(item);
                }
            });
		}
		return persisted;
	}
}
