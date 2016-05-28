package com.timekeeping.schedule.support;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Service provides high-level data access to {@link Schedule} objects
 * and other {@link Schedule}-related operations.
 * 
 * @author Mikhail Romanenko
 */
@Service
@Transactional
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	/**
	 * Find particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shopId
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 * @return {@link Schedule} entity, otherwise returns null.
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ADMIN') || hasPermission(#shopId, 'read')")
	public Schedule findSchedule(long shopId, LocalDate date) {
		return scheduleRepository.findByShopIdAndDate(shopId, date);
	}

	/**
	 * Save passed {@link Schedule} object.
     * If schedule already exists - update it corresponding to passed schedule.
	 * 
	 * @param schedule
	 *            schedule to save/update.
     * @return saved or updated schedule.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PreAuthorize("hasRole('ADMIN') || hasPermission(#schedule.shop.id, 'read')")
	public Schedule saveSchedule(Schedule schedule) {
		Assert.notNull(schedule);
		return scheduleRepository.saveOrUpdate(schedule);
	}

	/**
	 * Remove particular {@link Schedule} entity corresponding to the given
	 * {@code shopId} and {@code date} parameters.
	 * 
	 * @param shopId
	 *            {@code long}-value id of the {@link Shop} entity.
	 * @param date
	 *            {@code LocalDate} of the schedule. Must be not null.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	public void removeSchedule(long shopId, LocalDate date) {
		scheduleRepository.deleteByShopIdAndDate(shopId, date);
	}

}
