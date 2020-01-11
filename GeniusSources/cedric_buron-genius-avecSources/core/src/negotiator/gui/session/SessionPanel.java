package negotiator.gui.session;

import java.awt.BorderLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import listener.Listener;
import negotiator.AgentID;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiatorException;
import negotiator.gui.progress.session.ActionDocumentModel;
import negotiator.gui.progress.session.OutcomesListModel;
import negotiator.gui.progress.session.SessionProgressUI;
import negotiator.logging.ConsoleLogger;
import negotiator.logging.FileLogger;
import negotiator.logging.XmlLogger;
import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.SessionsInfo;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.ParticipantRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.MultilateralSessionConfiguration;
import negotiator.session.Participant;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.session.TournamentManager;

/**
 * Session Panel. Asks user to configure a session. When user presses run, the
 * panel changes into a progress panel and a session runner is started.
 */
@SuppressWarnings("serial")
public class SessionPanel extends JPanel {
	public SessionPanel() {
		final SessionModel model = new SessionModel();

		setLayout(new BorderLayout());
		add(new SessionConfigPanel(model), BorderLayout.CENTER);
		model.addListener(new Listener<MultilateralSessionConfiguration>() {
			@Override
			public void notifyChange(final MultilateralSessionConfiguration config) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean showChart = model.getShowChart().getValue();
						boolean biChart = model.getParticipantsModel().getSize() == 2
								&& model.getBilateralUtilUtilPlot().getValue();
						runSession(config, showChart, biChart, model.getBilateralShowAllBids().getValue(),
								model.getPrintEnabled().getValue());
					}
				}).start();
			}

		});

	}

	/**
	 * Runs a session and waits for completion.
	 * 
	 * @param config
	 * @param showChart
	 *            true iff a progress chart should be shown.
	 * @param useBiChart
	 *            true iff the bilateral progress chart is to be used.
	 * @param showAllBids
	 */
	private void runSession(MultilateralSessionConfiguration config, boolean showChart, boolean useBiChart,
			boolean showAllBids, boolean isPrintEnabled) {
		System.out.println("run session, with " + config);
		try {
			start(config, showChart, useBiChart, showAllBids, isPrintEnabled);
		} catch (InstantiateException | RepositoryException | NegotiatorException | XMLStreamException | IOException
				| TimeoutException | ExecutionException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Session failed to run: " + e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

	}

	/**
	 * 
	 * @param config
	 * @param showChart
	 * @param showBiChart
	 *            true if progress chart has to be shown
	 * @param showAllBids
	 *            if the bilateral progress chart should be used. Ignored if
	 *            showBiChart is false.
	 * @param isPrintEnabled
	 *            true iff system out print is enabled.
	 * @throws InstantiateException
	 * @throws RepositoryException
	 * @throws NegotiatorException
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 */
	public void start(MultilateralSessionConfiguration config, boolean showChart, boolean showBiChart,
			boolean showAllBids, boolean isPrintEnabled) throws InstantiateException, RepositoryException,
			NegotiatorException, XMLStreamException, IOException, TimeoutException, ExecutionException {

		if (config.getParties().size() < 2) {
			throw new IllegalArgumentException("There should be at least two negotiating agents !");
		}

		MultilateralProtocol protocol = TournamentManager.getProtocol(config.getProtocol());
		SessionsInfo info = new SessionsInfo(protocol, config.getPersistentDataType(), isPrintEnabled);
		Session session = new Session(config.getDeadline(), info);

		ExecutorWithTimeout executor = new ExecutorWithTimeout(1000 * config.getDeadline().getTimeOrDefaultTimeout());
		List<NegotiationPartyInternal> negoparties = getNegotiationParties(config, session, info, executor);
		SessionManager sessionManager = new SessionManager(negoparties, session, executor);

		displayProgress(negoparties, sessionManager, showChart, showBiChart, showAllBids);

		// connect the loggers.
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String fileName = String.format("log/Log-Session_%s", dateFormat.format(new Date()));
		FileLogger filelogger = new FileLogger(fileName);
		XmlLogger xmlLogger = new XmlLogger(new FileOutputStream(fileName + ".xml"), "Session");
		sessionManager.addListener(filelogger);
		sessionManager.addListener(xmlLogger);
		sessionManager.addListener(new ConsoleLogger());

		System.out.println("Negotiation session has started.");
		Thread t = new Thread(sessionManager);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// close the loggers
		System.out.println("Negotiation session has stopped.");
		try {
			filelogger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			xmlLogger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		info.close();

	}

	/**
	 * 
	 * @param negoparties
	 * @param sessionManager
	 * @param showChart
	 *            true iff any progress chart has to be shown
	 * @param biChart
	 *            if the bilateral progress chart has to be shown. Ignored if
	 *            showChart is false.
	 * @param showAllBids
	 */
	private void displayProgress(List<NegotiationPartyInternal> negoparties, SessionManager sessionManager,
			boolean showChart, boolean biChart, boolean showAllBids) {
		OutcomesListModel model = new OutcomesListModel(negoparties);
		ActionDocumentModel actiondocument = new ActionDocumentModel();
		sessionManager.addListener(model);
		sessionManager.addListener(actiondocument);
		removeAll();
		add(new SessionProgressUI(model, actiondocument, showChart, biChart, showAllBids), BorderLayout.CENTER);
		revalidate();
	}

	/**
	 * 
	 * @param config
	 * @param session
	 * @param info
	 * @param executor
	 *            the executor in which this session runs.
	 * @return the parties for this negotiation. Converts the config into actual
	 *         {@link NegotiationParty}s
	 * @throws RepositoryException
	 * @throws NegotiatorException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	private List<NegotiationPartyInternal> getNegotiationParties(MultilateralSessionConfiguration config,
			Session session, SessionsInfo info, ExecutorWithTimeout executor)
			throws RepositoryException, NegotiatorException, TimeoutException, ExecutionException {
		List<ParticipantRepItem> parties = new ArrayList<>();
		List<ProfileRepItem> profiles = new ArrayList<>();
		List<AgentID> names = new ArrayList<AgentID>();

		for (Participant participant : config.getParties()) {
			ParticipantRepItem strategy = participant.getStrategy();
			parties.add(strategy);
			if (!strategy.isMediator()) {
				profiles.add(participant.getProfile());
				names.add(participant.getId());
			}
		}

		return TournamentManager.getPartyList(executor, config, info, session);
		// List<NegotiationPartyInternal> negoparties =
		// TournamentGenerator.generateSessionParties(parties, profiles,
		// names, session, info);
		// return negoparties;
	}

	private List<NegotiationPartyInternal> getNonMediators(List<NegotiationPartyInternal> negoparties) {
		List<NegotiationPartyInternal> list = new ArrayList<>();
		for (NegotiationPartyInternal party : negoparties) {
			if (!party.isMediator()) {
				list.add(party);
			}
		}
		return list;
	}

	/**
	 * simple stub to run this stand-alone (for testing).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame gui = new JFrame();
		gui.setLayout(new BorderLayout());
		gui.getContentPane().add(new SessionPanel(), BorderLayout.CENTER);
		gui.pack();
		gui.setVisible(true);
	}

}
