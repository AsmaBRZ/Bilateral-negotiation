package negotiator.logging;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import listener.Listener;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Offer;
import negotiator.events.AgentLogEvent;
import negotiator.events.MultipartyNegoActionEvent;
import negotiator.events.MultipartySessionEndedEvent;
import negotiator.events.NegotiationEvent;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.session.Session;
import negotiator.xml.Key;
import negotiator.xml.XmlWriteStream;

/**
 * Creates a logger which will log {@link NegotiationEvent}s to a XML file. Logs
 * the {@link MultipartySessionEndedEvent}.
 */
public class XmlLogger implements Listener<NegotiationEvent>, Closeable {
	protected XmlWriteStream stream;
	/**
	 * map<key,value> where keys are the agent names. The values are the logs
	 * returned by the agent through an {@link AgentLogEvent}.
	 */
	protected Map<String, Map<Object, Object>> agentLogs = new HashMap<>();
	int nrOffers = 0;
	/**
	 * The agent that did the first action. Null until a first action was done
	 * in the current session or if there is no current session.
	 */
	AgentID startingAgent = new AgentID("-");

	/**
	 * @param out
	 *            {@link OutputStream} to write the log to. If this is a file,
	 *            we recommend to use the extension ".xml". This logger becomes
	 *            owner of this outputstream and will close it eventually.
	 * @param topLabel
	 *            the top level label to use in the output file
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public XmlLogger(OutputStream out, String topLabel) throws FileNotFoundException, XMLStreamException {
		stream = new XmlWriteStream(out, topLabel);
	}

	@Override
	public void close() throws IOException {
		try {
			stream.flush();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		stream.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void notifyChange(NegotiationEvent e) {
		try {
			if (e instanceof MultipartyNegoActionEvent) {
				if (startingAgent == null) {
					startingAgent = ((MultipartyNegoActionEvent) e).getAction().getAgent();
				}
				if (((MultipartyNegoActionEvent) e).getAction() instanceof Offer) {
					nrOffers++;
				}
			} else if (e instanceof AgentLogEvent) {
				// Map<String,String> to Map<Object,Object>...
				agentLogs.put(((AgentLogEvent) e).getAgent(), (Map<Object, Object>) (Map) ((AgentLogEvent) e).getLog());
			} else if (e instanceof MultipartySessionEndedEvent) {
				stream.write("NegotiationOutcome", getOutcome((MultipartySessionEndedEvent) e));
				stream.flush();
				// log done, reset the per-session trackers
				agentLogs = new HashMap<>();
				nrOffers = 0;
				startingAgent = null;
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @param e
	 *            the {@link MultipartySessionEndedEvent}
	 * @return the complete session outcome, including all agent outcomes
	 */
	private Map<Object, Object> getOutcome(MultipartySessionEndedEvent e) {
		Map<Object, Object> outcome = new HashMap<>();

		Session session = e.getSession();
		outcome.put("currentTime", new Date().toString());
		outcome.put("startingAgent", startingAgent.toString());
		outcome.put("bids", nrOffers);

		outcome.put("lastAction", session.getMostRecentAction());
		outcome.put("deadline", session.getDeadlines().valueString());
		outcome.put("runtime", session.getRuntimeInSeconds());
		outcome.put("domain", e.getParties().get(0).getUtilitySpace().getDomain().getName());
		outcome.put("finalOutcome", e.getAgreement() == null ? "-" : e.getAgreement());

		if (e.getAgreement() != null) {
			outcome.put("timeOfAgreement", session.getTimeline().getTime());
		}

		outcome.putAll(getAgentResults(e.getParties(), e.getAgreement()));
		return outcome;
	}

	/**
	 * 
	 * @param parties
	 *            the parties in the negotiation
	 * @param bid
	 *            the accepted bid, or null
	 * @return a Map containing all party results as key-value pairs
	 */
	private Map<Object, Object> getAgentResults(List<NegotiationPartyInternal> parties, Bid bid) {
		Map<Object, Object> outcome = new HashMap<>();

		for (NegotiationPartyInternal party : parties) {
			outcome.put(new Key("resultsOfAgent"), partyResults(party, bid));
		}

		return outcome;
	}

	/**
	 * Collect the results of a party in a map. This map will also contain the
	 * logs as done by the agent.
	 * 
	 * @param party
	 *            the party to collect the results for
	 * @param bid
	 *            the accepted bid, or null
	 * @return a map containing the results
	 */
	private Map<Object, Object> partyResults(NegotiationPartyInternal party, Bid bid) {
		Map<Object, Object> outcome = new HashMap<>();
		if (agentLogs.containsKey(party.getID())) {
			outcome.putAll(agentLogs.get(party.getID()));
		}
		outcome.put("agent", party.getID());
		outcome.put("agentClass", party.getParty().getClass().getName());
		outcome.put("agentDesc", party.getParty().getDescription());
		outcome.put("utilspace", party.getUtilitySpace().getName());
		outcome.put("discount", party.getUtilitySpace().discount(1.0, 1.0));
		outcome.put("finalUtility", party.getUtility(bid));
		outcome.put("discountedUtility", party.getUtilityWithDiscount(bid));
		return outcome;
	}

}
