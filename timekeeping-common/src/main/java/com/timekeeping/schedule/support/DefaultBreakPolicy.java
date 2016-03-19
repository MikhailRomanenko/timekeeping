package com.timekeeping.schedule.support;

import java.util.function.UnaryOperator;

import org.springframework.stereotype.Service;

/**
 * See {@link BreakPolicy}.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
public class DefaultBreakPolicy implements BreakPolicy {

	@Override
	public UnaryOperator<Integer> mapper() {
		return v -> {
			if (v <= 60)
				return v;
			else if (v > 60 && v <= 240)
				return v - 30;
			else if (v > 240 && v <= 540)
				return v - 60;
			else
				return v - 90;
		};
	}
}
