package panels;

import listener.DefaultListenable;

/**
 * Stores a listen-able boolean value. Listeners always hear the current value,
 * not the lock setting.
 *
 */
public class BooleanModel extends DefaultListenable<Boolean> {
	private boolean value = false;
	private boolean lock = false;

	/**
	 * 
	 * @param b
	 *            the initial value for the boolean
	 */
	public BooleanModel(boolean b) {
		this.value = b;
	}

	/**
	 * @param newValue
	 *            the new value. Check {@link #isLocked()} before attempting
	 *            this
	 * @throws IllegalStateException
	 *             if object is locked.
	 */
	public void setValue(boolean newValue) {
		if (lock) {
			throw new IllegalStateException("Value is locked");
		}
		if (value != newValue) {
			value = newValue;
			notifyChange(value);
		}
	}

	public void setLock(boolean isLock) {
		lock = isLock;
		notifyChange(value);
	}

	/**
	 * @return true if the value is locked and can't be changed.
	 */
	public boolean isLocked() {
		return lock;
	}

	public boolean getValue() {
		return value;
	}
}
