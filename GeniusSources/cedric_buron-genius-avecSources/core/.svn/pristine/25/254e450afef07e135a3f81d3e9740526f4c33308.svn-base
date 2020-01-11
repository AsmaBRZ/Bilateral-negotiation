package listener;

/**
 * Simple reusable listenable
 *
 * @param <TYPE>
 *            the type of the data being passed around.
 */
public interface Listenable<TYPE> {
	/**
	 * Add listener for this observable
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addListener(Listener<TYPE> l);

	/**
	 * Remove listener for this observable
	 * 
	 * @param l
	 *            the listener to remove
	 */

	public void removeListener(Listener<TYPE> l);

	/**
	 * notify a change in the data. This notifies all the listeners of the
	 * change.
	 * 
	 * FIXME notifyChange really should be accessible only for owners, not for
	 * outsiders.
	 * 
	 * @param data
	 *            an optional value to pass to our listeners.
	 */
	public void notifyChange(TYPE data);
}
