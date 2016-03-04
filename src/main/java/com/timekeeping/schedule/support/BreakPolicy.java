package com.timekeeping.schedule.support;

import java.util.function.UnaryOperator;

public interface BreakPolicy {
	UnaryOperator<Integer> mapper();
}
