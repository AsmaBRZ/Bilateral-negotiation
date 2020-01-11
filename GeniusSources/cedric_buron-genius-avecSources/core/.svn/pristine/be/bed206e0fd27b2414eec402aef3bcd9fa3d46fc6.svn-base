package negotiator.session;

import static java.lang.String.format;
import static misc.ConsoleHelper.useConsoleOut;
import static misc.Time.prettyTimeSpan;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import list.Tuple;
import listener.DefaultListenable;
import listener.Listenable;
import listener.Listener;
import negotiator.AgentID;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.events.AgreementEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.events.TournamentEndedEvent;
import negotiator.events.TournamentSessionStartedEvent;
import negotiator.events.TournamentStartedEvent;
import negotiator.exceptions.AnalysisException;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.SessionsInfo;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.timeline.Timeline;
import negotiator.tournament.SessionConfigurationList;

/**
 * Manages a multi-lateral tournament and makes sure that the
 * {@link negotiator.session.SessionManager} are instantiated. It uses the
 * configuration object which is created by the user interface and extracts
 * individual session from configuration object which it wil pass on to the
 * session manager.
 * 
 * <p>
 * Agents in a tournament must be of class {@link NegotiationParty}.
 */
public class TournamentManager extends Thread implements Listenable<NegotiationEvent> {

	/**
	 * Holds the configuration used by this tournament manager
	 */
	private SessionConfigurationList sessionConfigurationsList;

	/**
	 * Used to silence and restore console output for agents
	 */
	PrintStream orgOut = System.out;
	PrintStream orgErr = System.err;

	/**
	 * Used for printing time related console output.
	 */
	long start = System.nanoTime();

	/**
	 * The actual handler for Listenable. We would really want multiple
	 * inheritance here...
	 */
	private Listenable<NegotiationEvent> listeners = new DefaultListenable<>();

	private SessionsInfo info;

	/**
	 * Initializes a new instance of the
	 * {@link negotiator.session.TournamentManager} class. The tournament
	 * manager uses the provided configuration to find which sessions to run and
	 * how many collections of these sessions (tournaments) to run.
	 *
	 * @param config
	 *            The configuration to use for this Tournament
	 * @throws InstantiateException
	 * @throws IOException
	 */
	public TournamentManager(MultilateralTournamentConfiguration config) throws IOException, InstantiateException {
		sessionConfigurationsList = new SessionConfigurationList(config);
		info = new SessionsInfo(getProtocol(config.getProtocolItem()), config.getPersistentDataType(),
				config.isPrintEnabled());
		if (sessionConfigurationsList.size().bitLength() > 31) {
			throw new InstantiateException(
					"Configuration results in more than 2 billion runs: " + sessionConfigurationsList.size());
		}

	}

	/****************** listener support *******************/
	@Override
	public void addListener(Listener<NegotiationEvent> listener) {
		listeners.addListener(listener);
	}

	@Override
	public void removeListener(Listener<NegotiationEvent> listener) {
		listeners.removeListener(listener);
	}

	/**
	 * this is not available for outsiders. Throw right away.
	 * 
	 * @param evt
	 */
	@Override
	public void notifyChange(NegotiationEvent evt) {
		throw new IllegalAccessError();
	}

	/****************** manager *****************************/

	/**
	 * Runnable implementation for thread
	 */
	@Override
	public void run() {
		start = System.nanoTime();
		try {
			this.runSessions();
			System.out.println("Tournament completed");
			System.out.println("------------------");
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Tournament exited with an error");
			System.out.println("------------------");
			System.out.println("");

		}
		long end = System.nanoTime();
		System.out.println("Run finished in " + prettyTimeSpan(end - start));
	}

	/**
	 * Run all sessions in the given generator.
	 * 
	 * @param tournamentNumber
	 * @param sessionsInfo
	 * @param info
	 *            shared {@link SessionsInfo}
	 * @throws ArithmeticException
	 *             if number of sessions does not fit in a 32 bit int
	 */
	private void runSessions() {
		int sessionNumber = 0;
		int tournamentNumber = 1; // FIXME
		int totalSessions = sessionConfigurationsList.size().intValueExact();
		listeners.notifyChange(new TournamentStartedEvent(tournamentNumber, totalSessions));

		for (SessionConfiguration sessionInfo : sessionConfigurationsList) {

			final Session session = new Session(sessionInfo.getDeadline(), info);
			String errormessage = null; // null=all OK.
			List<NegotiationPartyInternal> partyList = null;

			sessionNumber++;
			listeners.notifyChange(new TournamentSessionStartedEvent(sessionNumber, totalSessions));

			ExecutorWithTimeout executor = new ExecutorWithTimeout(
					1000 * sessionInfo.getDeadline().getTimeOrDefaultTimeout());
			try {
				partyList = getPartyList(executor, sessionInfo, info, session);
			} catch (TimeoutException e) {
				System.err.println("failed to construct agent due to timeout:" + e.getMessage());
				listeners.notifyChange(new SessionFailedEvent(e, "failed to construct agent due to timeout"));
				continue; // do not run any further if we don't have the agents.
			} catch (ExecutionException e) {
				e.printStackTrace();
				listeners.notifyChange(new SessionFailedEvent(e, "failed to construct agent"));
				continue; // do not run any further if we don't have the agents.
			}

			Exception e = runSingleSession(partyList, executor);
			if (e != null) {
				listeners.notifyChange(new SessionFailedEvent(e, "failure while running session"));
				continue;
			}

			System.out.println(errormessage != null ? "Session done." : "Session exited.");
			int nDone = totalSessions * tournamentNumber + sessionNumber;
			int nRemaining = totalSessions - nDone;
			System.out
					.println(format("approx. %s remaining", prettyTimeSpan(estimatedTimeRemaining(nDone, nRemaining))));
			System.out.println("");

		}
		listeners.notifyChange(new TournamentEndedEvent());
	}

	/**
	 * Generate the parties involved in the next round of the tournament
	 * generator. Assumes generator.hasNext(). <br>
	 * Checks various error cases and reports accordingly. If repository fails
	 * completely, we call System.exit(). useConsoleOut is called to disable
	 * console output while running agent code. <br>
	 * 
	 * @param executor
	 *            the executor to use
	 * @param config
	 *            the {@link MultilateralSessionConfiguration} to use
	 * @param info
	 *            the global {@link SessionsInfo}.
	 * @return list of parties for next round. May return null if one or more
	 *         agents could not be created.
	 * @throws TimeoutException
	 *             if we run out of time during the construction.
	 * @throws ExecutionException
	 *             if one of the agents does not construct properly
	 */
	public static List<NegotiationPartyInternal> getPartyList(ExecutorWithTimeout executor,
			final MultilateralSessionConfiguration config, final SessionsInfo info, final Session session)
			throws TimeoutException, ExecutionException {

		final List<Participant> parties = new ArrayList<Participant>(config.getParties());
		if (config.getProtocol().getHasMediator()) {
			parties.add(0, config.getMediator());
		}

		// add AgentIDs
		final List<Tuple<AgentID, Participant>> partiesWithId = new ArrayList<>();
		for (Participant party : parties) {
			AgentID id = AgentID.generateID(party.getStrategy().getUniqueName());
			partiesWithId.add(new Tuple<>(id, party));
		}

		useConsoleOut(info.isPrintEnabled());

		try {
			return executor.execute("createpartylist", new Callable<List<NegotiationPartyInternal>>() {
				@Override
				public List<NegotiationPartyInternal> call() throws RepositoryException, NegotiatorException {
					List<NegotiationPartyInternal> partieslist = new ArrayList<>();
					for (Tuple<AgentID, Participant> p : partiesWithId) {
						partieslist.add(new NegotiationPartyInternal(p.get2().getStrategy(), p.get2().getProfile(),
								session, info, p.get1()));
					}
					return partieslist;
				}

			});
		} finally {
			useConsoleOut(true);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Run a single session for the given parties (protocol and session are also
	 * used, but extracted from the tournament manager's configuration
	 *
	 * @param parties
	 *            the parties to run the tournament for. Must contain at least 1
	 *            party. All parties must not be null.
	 * 
	 * @return If all goes ok, returns null. Else, Exception that occured during
	 *         execution.
	 */
	public Exception runSingleSession(List<NegotiationPartyInternal> parties, ExecutorWithTimeout executor) {
		if (parties == null || parties.isEmpty()) {
			throw new IllegalArgumentException("parties list doesn't contain a party");
		}
		for (NegotiationPartyInternal party : parties) {
			if (party == null) {
				throw new IllegalArgumentException("parties contains a null party:" + parties);
			}
		}
		Exception exception = null;

		try {
			Session session = parties.get(0).getSession();

			// TODO: ** hackery ** we should make sure that session gives
			// timeline to agents, not the other way around.
			Timeline timeline = parties.get(0).getTimeLine();
			session.setTimeline(timeline);
			SessionManager sessionManager = new SessionManager(parties, session, executor);
			sessionManager.addListener(new Listener<NegotiationEvent>() {
				@Override
				public void notifyChange(NegotiationEvent data) {
					listeners.notifyChange(data);
				}
			});
			setPrinting(false);
			sessionManager.runAndWait();
			setPrinting(true);

			try {
				double runTime = session.getRuntimeInSeconds();
				listeners.notifyChange(new AgreementEvent(session, info.getProtocol(), parties, runTime));
			} catch (Error e) {
				exception = new AnalysisException("Unknown error in analyses or logging", e);
			}
		} catch (Exception e) {
			exception = e;
		}

		setPrinting(true);
		if (exception != null) {
			exception.printStackTrace();
		}
		return exception;
	}

	/**
	 * Tries to switch the console output to the preferred setting. Only
	 * possible if {@link SessionsInfo#isPrintEnabled} is true.
	 * 
	 * @param isPreferablyEnabled
	 *            true if we'd like to have print to stdout enabled, false if
	 *            preferred disabled.
	 */
	private void setPrinting(boolean isPreferablyEnabled) {
		if (info.isPrintEnabled()) {
			// if enabled, we ignore the setPrinting preferences so
			// that all printing stays enabled.
			return;
		}
		useConsoleOut(isPreferablyEnabled);
	}

	/**
	 * Calculate estimated time remaining using extrapolation
	 *
	 * @return estimation of time remaining in nano seconds
	 */
	private double estimatedTimeRemaining(int nSessionsDone, int nSessionsRemaining) {
		long now = System.nanoTime() - start;
		double res = nSessionsRemaining * now / (double) nSessionsDone;
		return res;
	}

	/**
	 * Create a new instance of the Protocol object from a
	 * {@link MultiPartyProtocolRepItem}
	 *
	 * @return the created protocol.
	 * @throws InstantiateException
	 *             if failure occurs while constructing the rep item.
	 */
	public static MultilateralProtocol getProtocol(MultiPartyProtocolRepItem protocolRepItem)
			throws InstantiateException {

		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class<?> protocolClass;
		try {
			protocolClass = loader.loadClass(protocolRepItem.getClassPath());

			Constructor<?> protocolConstructor = protocolClass.getConstructor();

			return (MultilateralProtocol) protocolConstructor.newInstance();
		} catch (Exception e) {
			throw new InstantiateException("failed to instantiate " + protocolRepItem, e);
		}

	}

}
