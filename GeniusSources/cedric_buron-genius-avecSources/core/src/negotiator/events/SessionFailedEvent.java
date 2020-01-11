package negotiator.events;

/**
 * Indicates that a session failed, typically due to an exception, timeout etc.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class SessionFailedEvent implements NegotiationEvent {

	private final Exception exception;
	private final String message;

	/**
	 * @param e
	 *            the exception that caused the problem. May be null.
	 * @param mes
	 *            message
	 */
	public SessionFailedEvent(Exception e, String mes) {
		exception = e;
		message = mes;
	}

	public Exception getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return message + ":" + exception;
	}

}
