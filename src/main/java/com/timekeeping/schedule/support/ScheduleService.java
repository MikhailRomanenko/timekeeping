package com.timekeeping.schedule.support;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;

/**
 * Service providing high-level data access and other {@link Schedule}-related operations.
 * 
 * @author Mikhail Romanenko
 *
 */

@Service
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	
	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}
	
	/**
	 * Finds particular {@link Schedule} entity corresponding to the 
	 * given {@code shop} and {@code date} parameters.
	 * 
	 * @param shop {@link Shop} entity.
	 * @param date {@code LocalDate} of the schedule.
	 * @return {@link Schedule} entity, otherwise returns {@code null}.
	 */
	public Schedule findSchedule(Shop shop, LocalDate date) {
		return scheduleRepository.findByShopAndDate(shop, date);
	}
	
	/**
	 * Saves {@link Schedule} object or updates its {@link ScheduleItem}
	 * set with provided {@code schedule} object.
	 * 
	 * @param schedule {@link Schedule} object to save.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public void saveSchedule(Schedule schedule) {
		//TODO Test logic
		if(schedule.getId() != null){
			updateItems(schedule);
		} else {
			scheduleRepository.save(schedule);
		}
		
	}

	/**
	 * Updates {@link ScheduleItem}s set of the existing {@link Schedule} entity
	 * with provided {@code schedule}'s set. 
	 * 
	 * @param schedule
	 */
	protected void updateItems(Schedule schedule) {
		//TODO Test logic
		Schedule scheduleToUpdate = scheduleRepository.findOne(schedule.getId());
		scheduleToUpdate.updateItems(schedule.getItems());
		scheduleRepository.save(scheduleToUpdate);
	}

}
