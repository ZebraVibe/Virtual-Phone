package com.ouken.phone.utils.math;

public enum AmountState {
	// added
	ADDED, ADDED_BUT_REACHED_EXACTLY_CAP, 
	ADDED_BUT_REACHED_EXACTLY_ZERO,

	ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP, 
	ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO,

	// not added
	NOT_ADDED_SINCE_ALREADY_CAP, 
	NOT_ADDED_SINCE_ALREADY_ZERO,

	NOT_ADDED_SINCE_WENT_ABOVE_CAP, 
	NOT_ADDED_SINCE_WENT_BELOW_ZERO;

	// added
	public boolean isOverallAdded() {
		return this == ADDED || this == ADDED_BUT_REACHED_EXACTLY_CAP || this == ADDED_BUT_REACHED_EXACTLY_ZERO
				|| this == ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP || this == ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO;
	}

	public boolean isOverallReducedAdded() {
		return this == ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP || this == ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO;
	}

	public boolean isAdded() {
		return this == ADDED;
	}

	public boolean isAddedButReachedExactlyCap() {
		return this == ADDED_BUT_REACHED_EXACTLY_CAP;
	}

	public boolean isAddedButReachedExactlyZero() {
		return this == ADDED_BUT_REACHED_EXACTLY_ZERO;
	}

	public boolean isAddedButReducedSinceWentAboveCap() {
		return this == ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP;
	}

	public boolean isAddedButReducedSinceWentBelowZero() {
		return this == ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO;
	}

	// not added
	public boolean isOverallNotAdded() {
		return this == NOT_ADDED_SINCE_ALREADY_CAP || this == NOT_ADDED_SINCE_ALREADY_ZERO
				|| this == NOT_ADDED_SINCE_WENT_ABOVE_CAP || this == NOT_ADDED_SINCE_WENT_BELOW_ZERO;
	}

	public boolean isNotAddedSinceAlreadyCap() {
		return this == NOT_ADDED_SINCE_ALREADY_CAP;
	}

	public boolean isNotAddedSinceAlreadyZero() {
		return this == NOT_ADDED_SINCE_ALREADY_ZERO;
	}

	public boolean isNotAddedSincWentAboveCap() {
		return this == NOT_ADDED_SINCE_WENT_ABOVE_CAP;
	}

	public boolean isNotAddedSinceWentBelowZero() {
		return this == NOT_ADDED_SINCE_WENT_BELOW_ZERO;
	}
}
