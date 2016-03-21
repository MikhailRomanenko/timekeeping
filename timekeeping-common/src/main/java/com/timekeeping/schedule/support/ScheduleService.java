package com.timekeeping.schedule.support;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.schedule.ScheduleItem;
import com.timekeeping.shop.Shop;
import com.timekeeping.shop.support.ShopRepository;

/**
 * Service providing high-level data access and other {@link Schedule}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */

@Service
@Transactional
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ShopRepository shopRepository;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ShopRepository shopRepository) {
		this.scheduleRepository = scheduleRepository;
		this.shopRepository = shopRepository;
	}

	/**
	 * Finds particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shop
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 * @return {@link Schedule} entity, otherwise returns null.
	 */
	@Transactional(readOnly = true)
	public Schedule findSchedule(long shopId, LocalDate date) {
		return scheduleRepository.findByShopIdAndDate(shopId, date);
	}

	/**
	 * Saves {@link Schedule} object or updates its {@link ScheduleItem} set
	 * with provided items.
	 * 
	 * @param date
	 *            {@code LocalDate} of the schedule to save/update.
	 * @param shopId
	 *            id of the shop.
	 * @param items
	 *            {@code Set} of the {@code ScheduleItems} objects to save/update.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveSchedule(Schedule schedule) {
		Assert.notNull(schedule.getDate(), "The 'date' field must not be null");
		Assert.notNull(schedule.getItems(), "The 'items' field must not be null");
		Assert.notNull(schedule.getShop(), "The 'shop' field must not be null");
		Assert.isTrue(shopRepository.exists(schedule.getShop().getId()),
				"Shop with given ID does not exist");
		
		if(!sameVersions(schedule)) {
			throw new OptimisticLockingFailureException("Attempt to save stale copy of an object");
		}
		
		scheduleRepository.saveOrUpdate(schedule);
		
	}
	
	private boolean sameVersions(Schedule schedule) {
		int dbVersion = scheduleRepository.getVersionFor(schedule.getId());
		return schedule.getVersion() == dbVersion;
	}

	/**
	 * Removes particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shopId
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 */
	public void removeSchedule(long shopId, LocalDate date) {
		scheduleRepository.deleteByShopIdAndDate(shopId, date);
	}

}
