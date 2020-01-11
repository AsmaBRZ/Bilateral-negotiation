package negotiator.logging;

import negotiator.events.RecoverableSessionErrorEvent;
import listener.Listener;
import negotiator.events.NegotiationEvent;

/**
 * Logs additional info to the console that normally would be ignored.
 *
 */
public class ConsoleLogger implements Listener<NegotiationEvent> {

	@Override
	public void notifyChange(NegotiationEvent e) {
		if (e instanceof RecoverableSessionErrorEvent) {
			RecoverableSessionErrorEvent e1 = (RecoverableSessionErrorEvent) e;
			System.err.println("There is an issue with " + e1.getParty() + " (" + e1.getParty().getParty()
					+ ") in session " + e1.getSession());
			e1.getException().printStackTrace();
		}

	}

}
