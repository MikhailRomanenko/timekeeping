package com.timekeeping.schedule.support;

import java.util.function.UnaryOperator;

import org.springframework.stereotype.Service;

/**
 * Service class to apply breaks policy to working hours.
 * Provides {@link UnaryOperator} implementations that used to calculate 
 * working hours from the time totally spent at work.
 * @author Mikhail Romanenko
 *
 */
@Service
public class BreakPolicy {

	private UnaryOperator<Integer> withoutBreakMapper = v -> v;
	
	/**
	 * Provides mapper that does not affect to working time.
	 */
	public UnaryOperator<Integer> withoutBreak() {
		return withoutBreakMapper;
	}
	
	/**
	 * Provides mapper that subtracts break time from working time.
	 * Break time depends on working time value as follows:
	 * - if working time less then 60 minutes then break value is 0 minutes;
	 * - if working time between 60 and 240 minutes then break value is 30 minutes;
	 * - if working time between 241 and 540 minutes then break value is 60 minutes;
	 * - if working time more then 540 minutes then break value is 90 minutes;
	 */
	public UnaryOperator<Integer> withDefaultBreak() {
		return v -> {
			if(v <= 60) return v;
			else if(v > 60 && v <= 240) return v - 30;
			else if(v > 240 && v <= 540) return v - 60;
			else return v - 90;
		};
	}
}
