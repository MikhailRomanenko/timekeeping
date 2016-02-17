package com.timekeeping.schedule.support;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.schedule.ScheduleItemId;

/**
 * Spring Data repository for {@link ScheduleItem} entities.
 * For more information see {@link JpaRepository}.
 * 
 * @author Mikhail Romanenko
 *
 */

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, ScheduleItemId> {

}
