package negotiator.logging;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import listener.Listener;
import negotiator.events.AgreementEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEvent;

/**
 * Creates a file logger which wil log the inputted messages to a file
 */
public class FileLogger implements Listener<NegotiationEvent>, Closeable {
	// The internal print stream used for file writing
	PrintStream ps;

	/**
	 * 
	 * @param fileName
	 *            the log file without the .csv extension
	 * @throws FileNotFoundException
	 */
	public FileLogger(String fileName) throws FileNotFoundException {
		ps = new PrintStream(fileName + ".csv");
	}

	@Override
	public void close() throws IOException {
		ps.close();
	}

	@Override
	public void notifyChange(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			ps.println(((LogMessageEvent) e).getMessage());
		}
		if (e instanceof AgreementEvent) {
			ps.println(e.toString());
		}
		// MultipartyNegotiationOfferEvent ignored.
		// MultipartyNegotiationSessionEvent ignored.

	}
}
