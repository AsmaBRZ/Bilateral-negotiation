package listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * a default implementation for Listenable. Thread safe.
 *
 * @param <TYPE>
 *            the type of the data being passed around.
 */
public class DefaultListenable<TYPE> implements Listenable<TYPE> {
	private List<Listener<TYPE>> listeners = new CopyOnWriteArrayList<Listener<TYPE>>();

	@Override
	public void addListener(Listener<TYPE> l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(Listener<TYPE> l) {
		listeners.remove(l);
	}

	@Override
	public void notifyChange(TYPE data) {
		for (Listener<TYPE> l : listeners) {
			try {
				l.notifyChange(data);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
