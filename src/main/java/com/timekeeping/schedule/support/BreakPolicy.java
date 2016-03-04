package com.timekeeping.schedule.support;

import java.util.function.UnaryOperator;

/**
 * Abstraction representing a service capable of providing logic for calculation
 * of the working time without break.
 * 
 * @author Mikhail Romanenko
 *
 */
public interface BreakPolicy {

	/**
	 * Provides logic for mapping overall working type to working time without
	 * break.
	 * 
	 * @return {@link UnaryOperator} implementation which apply mapping logic to
	 *         {@code Integer} values.
	 */
	UnaryOperator<Integer> mapper();
}
