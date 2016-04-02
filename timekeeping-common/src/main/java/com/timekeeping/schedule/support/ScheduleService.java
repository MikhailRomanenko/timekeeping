package com.timekeeping.schedule.support;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.timekeeping.schedule.Schedule;
import com.timekeeping.shop.Shop;

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
	private final ScheduleAdapter scheduleAdapter;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ScheduleAdapter scheduleAdapter) {
		this.scheduleRepository = scheduleRepository;
		this.scheduleAdapter = scheduleAdapter;
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
	public Schedule findSchedule(long shopId, LocalDate date) {
		return scheduleRepository.findByShopIdAndDate(shopId, date);
	}

	/**
	 * Save {@link Schedule} object represented by passed {@link ScheduleView} value object.
	 * In case schedule already exists - update its items corresponding to passed view.
	 * 
	 * @param scheduleView
	 *            {@code ScheduleView} of the schedule to save/update.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveSchedule(ScheduleView scheduleView) {
		Assert.notNull(scheduleView);
		Schedule schedule = null;
		if(scheduleRepository.exists(scheduleView.getShopId(), scheduleView.getDate())) {
			if(isVersionModified(scheduleView)) {
				throw new OptimisticLockingFailureException("Operating a stale copy of an object");
			}
			schedule = scheduleAdapter.updateSchedule(scheduleView);
		} else {
			schedule = scheduleAdapter.createSchedule(scheduleView);
		}
		scheduleRepository.save(schedule);
	}
	
	private boolean isVersionModified(ScheduleView view) {
		int currentVersion = scheduleRepository.getVersionFor(view.getShopId(), view.getDate());
		return view.getVersion() != currentVersion;
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
	public void removeSchedule(long shopId, LocalDate date) {
		scheduleRepository.deleteByShopIdAndDate(shopId, date);
	}

}
