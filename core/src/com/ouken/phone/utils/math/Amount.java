package com.ouken.phone.utils.math;

import com.badlogic.gdx.utils.Logger;

public class Amount {

	// constants
	private static final Logger log = new Logger(Amount.class.getName(), Logger.DEBUG);

	// attributes
	private float amount;
	private float cap;
	private boolean hasCap;

	// constructors
	public Amount() {
	}

	public Amount(float amount) {
		this.amount = amount;
	}

	public Amount(float amount, float cap) {
		this.amount = amount;
		setCap(cap);
	}

	// public methods;
	/** [IMPORTANT] sets the amount without checking */
	public void set(float amount) {
		if (hasCap && amount > cap) {
			log.error("couldnt set amount sinc amount > cap");
			return;
		}
		this.amount = amount;
	}

	public float get() {
		return amount;
	}

	/**
	 * Returns the Ratio between amount and cap (amount / cap) - 0 if no cap was set
	 * or cap == 0
	 */
	public float getRatio() {
		return hasCap && cap > 0 ? amount / cap : 0;
	}

	/** adds the amount with check == true */
	public void incrBy(float value, boolean allowReduction) {
		addChecked(Math.abs(value), allowReduction);
	}

	/** adds the amount with check == true */
	public void decrBy(float value, boolean allowReduction) {
		addChecked(-Math.abs(value), allowReduction);
	}

	/** increases the amount by 1 with check == true && allowReduciton == true */
	public void incr() {
		addChecked(1, true);
	}

	/** decreases the amount by 1 with check == true && allowReduction = true */
	public void decr() {
		addChecked(-1, true);
	}

	/** sets cap to Math.abs(cap). Even cap = 0 is considered as setting a cap */
	public void setCap(float cap) {
		if (cap < amount) {
			log.error("couldnt set cap - since cap < current amount");
			return;
		}
		this.cap = Math.abs(cap);
		hasCap = true;
	}

	public void removeCap() {
		hasCap = false;
		cap = 0;
	}

	/** returns -1 if no cap was set */
	public float getCap() {
		return hasCap ? cap : -1;
	}

	public boolean hasCap() {
		return hasCap;
	}

	public boolean isCap() {
		return hasCap && compare(this.amount, cap) == 0;
	}

	public boolean isZero() {
		return this.amount == 0;
	}

	public boolean isAboveZeroAfter(float amount) {
		return this.amount + amount > 0;
	}

	public boolean isCapAfter(float amount, boolean exactly) {
		if (!hasCap)
			return false;
		if (exactly)
			return compare(this.amount + amount, cap) == 0;
		else
			return compare(this.amount + amount, cap) >= 0;

	}

	public boolean isZeroAfter(float amount, boolean exactly) {
		if (exactly)
			return compare(this.amount + amount, 0) == 0;
		else
			return compare(this.amount + amount, 0) <= 0;

	}

	public boolean isBetween(boolean borders) {
		if (borders)
			return compare(this.amount, 0) >= 0 && compare(this.amount, cap) <= 0;
		else
			return this.amount > 0 && this.amount < cap;
	}

	public boolean isBetweenAfter(float amount, boolean borders) {
		if (borders)
			return compare(this.amount + amount, 0) >= 0 && compare(this.amount + amount, cap) <= 0;
		else
			return this.amount + amount > 0 && this.amount + amount < cap;
	}

	public boolean isOnBorder() {
		return compare(amount, 0) == 0 && compare(amount, cap) == 0;
	}

	/**
	 * if check == true only adds if the newAmount is exactly >=0 and (if cap is
	 * present) exactly <= cap. <br>
	 * allowRedction is only considered if check == true
	 */
	public AmountState addAndReturnState(float amount, boolean check, boolean allowReduction) {
		if (check) {
			AmountState checkState = checkAmountState(amount, allowReduction);

			if (checkState == null) {
				log.error(
						"couldn't determine " + AmountState.class.getSimpleName() + " state. Is cap set ? " + (hasCap));
				return null;
			}

			if (checkState.isAdded()) {
				this.amount += amount;
			} else if (checkState.isAddedButReachedExactlyCap() || checkState.isAddedButReducedSinceWentAboveCap()) {
				this.amount = cap;
			} else if (checkState.isAddedButReachedExactlyZero() || checkState.isAddedButReducedSinceWentBelowZero()) {
				this.amount = 0;
			}

			return checkState;
		}
		this.amount += amount;
		return AmountState.ADDED;
	}

	/** {@link Amount#addAndReturnState(float, boolean, boolean)} */
	public AmountState addUncheckedAndGetState(float amount) {
		return addAndReturnState(amount, false, false);
	}

	/** {@link Amount#addAndReturnState(float, boolean, boolean)} */
	public AmountState addCheckedAndGetState(float amount, boolean allowReduction) {
		return addAndReturnState(amount, true, allowReduction);
	}

	/**
	 * Returns true if the amount could get added.<br>
	 * {@link Amount#addAndReturnState(float, boolean, boolean)}
	 */
	public boolean add(float amount, boolean check, boolean allowReduction) {
		AmountState state = addAndReturnState(amount, check, allowReduction);
		return state.isOverallAdded();
	}

	/** {@link Amount#add(float, boolean, boolean)} */
	public boolean addUnchecked(float amount) {
		return add(amount, false, false);
	}

	/** {@link Amount#add(float, boolean, boolean)} */
	public boolean addChecked(float amount, boolean allowReduction) {
		return add(amount, true, allowReduction);
	}

	/** checks if the amount is addable returns a state */
	public AmountState checkAmountState(float amount, boolean allowReduction) {

		// without cap
		if (!hasCap()) {
			if (isAboveZeroAfter(amount))
				return AmountState.ADDED;
			else if (isZeroAfter(amount, true))
				return AmountState.ADDED_BUT_REACHED_EXACTLY_ZERO;
			else if (isZeroAfter(amount, false)) {

				if (isZero())
					return AmountState.NOT_ADDED_SINCE_ALREADY_ZERO;
				else if (allowReduction)
					return AmountState.ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO;
				else
					return AmountState.NOT_ADDED_SINCE_WENT_BELOW_ZERO;

			}
		}

		// with cap
		if (isBetweenAfter(amount, false))
			return AmountState.ADDED;

		if (isBetween(false)) {

			if (isZeroAfter(amount, true))
				return AmountState.ADDED_BUT_REACHED_EXACTLY_ZERO;

			else if (isZeroAfter(amount, false))
				return allowReduction ? AmountState.ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO
						: AmountState.NOT_ADDED_SINCE_WENT_BELOW_ZERO;

			else if (isCapAfter(amount, true))
				return AmountState.ADDED_BUT_REACHED_EXACTLY_CAP;

			else if (isCapAfter(amount, false))
				return allowReduction ? AmountState.ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP
						: AmountState.NOT_ADDED_SINCE_WENT_ABOVE_CAP;

		} else if (isZero()) {

			if (isZeroAfter(amount, false))
				return AmountState.NOT_ADDED_SINCE_ALREADY_ZERO;

			else if (isCapAfter(amount, true))
				return AmountState.ADDED_BUT_REACHED_EXACTLY_CAP;

			else if (isCapAfter(amount, false))
				return allowReduction ? AmountState.ADDED_BUT_REDUCED_SINCE_WENT_ABOVE_CAP
						: AmountState.NOT_ADDED_SINCE_WENT_ABOVE_CAP;

		} else if (isCap()) {

			if (isZeroAfter(amount, true))
				return AmountState.ADDED_BUT_REACHED_EXACTLY_ZERO;

			else if (isZeroAfter(amount, false))
				return allowReduction ? AmountState.ADDED_BUT_REDUCED_SINCE_WENT_BELOW_ZERO
						: AmountState.NOT_ADDED_SINCE_WENT_BELOW_ZERO;

			else if (isCapAfter(amount, false))
				return AmountState.NOT_ADDED_SINCE_ALREADY_CAP;
		}

		return null;
	}

	@Override
	public String toString() {
		String s = "amount= " + amount + " (hasCap= " + hasCap + ") cap= " + cap + " ratio= " + getRatio();
		return s;
	}

	// -- private methods --
	private int compare(float f1, float f2) {
		return Float.compare(f1, f2);
	}

}
