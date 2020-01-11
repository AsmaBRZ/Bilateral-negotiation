package negotiator.session;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Test;

import agents.nastyagent.AddPersistentDataToStandard;
import agents.nastyagent.CheckStoredData;
import agents.nastyagent.RandomBid;
import agents.nastyagent.StoreAndRetrieve;
import agents.nastyagent.ThrowInChoose;
import agents.nastyagent.ThrowInConstructor;
import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.DeadlineType;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationParty;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.SessionsInfo;
import negotiator.persistent.PersistentDataType;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;

/**
 * Test if session manager correctly stores data
 *
 */
public class SessionStorageTest {

	private final String domain = "file:test/resources/partydomain/party_domain.xml";
	private final String profile = "file:test/resources/partydomain/party1_utility.xml";
	private static final Class<? extends NegotiationParty> OPPONENT = RandomBid.class;

	private DomainRepItem domainRepItem;
	private ProfileRepItem profileRepItem;
	private ExecutorWithTimeout executor = new ExecutorWithTimeout(3000);

	private Session session;
	private SessionsInfo info;

	@After
	public void after() {
		info.close();
	}

	@Test
	public void testWithStorageSerializableStorage() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.SERIALIZABLE, true);
		run(StoreAndRetrieve.class);

	}

	@Test(expected = ExecutionException.class)
	public void testRunWithDisabledStorage() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.DISABLED, true);
		run(StoreAndRetrieve.class);
	}

	@Test(expected = ExecutionException.class)
	public void testRunWithStandardStorage() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.STANDARD, true);
		run(StoreAndRetrieve.class);
	}

	@Test
	public void checkStoreContents() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.STANDARD, true);
		run(CheckStoredData.class);

	}

	@Test(expected = Exception.class) // unsupportedOperationException
	public void tryChangeStorage() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.STANDARD, true);
		run(AddPersistentDataToStandard.class);

	}

	@Test(expected = InstantiateException.class)
	public void firstPartyCrashDirectly() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.STANDARD, true);
		run(ThrowInConstructor.class);
	}

	@Test(expected = ExecutionException.class)
	public void firstPartyCrashInReceive() throws InstantiateException, ActionException, InterruptedException,
			ExecutionException, NegotiationPartyTimeoutException, IOException {
		info = new SessionsInfo(new StackedAlternatingOffersProtocol(), PersistentDataType.STANDARD, true);
		run(ThrowInChoose.class);
	}

	/**
	 * Run session with an agent that tries to store some serializable object
	 * and that checks that the storage works
	 * 
	 * @param partyClass
	 * 
	 * @throws MalformedURLException
	 * @throws InstantiateException
	 * @throws ActionException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws NegotiationPartyTimeoutException
	 */
	private void run(Class<? extends NegotiationParty> partyClass) throws MalformedURLException, InstantiateException,
			ActionException, InterruptedException, ExecutionException, NegotiationPartyTimeoutException {
		session = new Session(new Deadline(180, DeadlineType.ROUND), info);
		domainRepItem = new DomainRepItem(new URL(domain));
		profileRepItem = new ProfileRepItem(new URL(profile), domainRepItem);

		List<NegotiationPartyInternal> theparties = generateParties(partyClass);
		SessionManager sessionMgr = new SessionManager(theparties, session, executor);

		sessionMgr.runAndWait();

		// run twice. 2nd time, it should check the data
		theparties = generateParties(partyClass);
		sessionMgr = new SessionManager(theparties, session, executor);

		sessionMgr.runAndWait();
	}

	private List<NegotiationPartyInternal> generateParties(Class<? extends NegotiationParty> partyClass)
			throws InstantiateException {
		ArrayList<NegotiationPartyInternal> parties = new ArrayList<NegotiationPartyInternal>();
		try {
			parties.add(createParty(partyClass));
			parties.add(createParty(OPPONENT));
		} catch (MalformedURLException | IllegalAccessException | ClassNotFoundException | RepositoryException
				| NegotiatorException | InstantiationException e) {
			throw new InstantiateException("Failed to create party " + partyClass, e);
		}
		return parties;
	}

	/**
	 * Create a real party based on the class
	 * 
	 * @param partyClass
	 * @return {@link NegotiationPartyInternal}
	 * @throws InstantiateException
	 *             if party can't be instantiated
	 */
	private NegotiationPartyInternal createParty(Class<? extends NegotiationParty> partyClass)
			throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			RepositoryException, NegotiatorException, InstantiateException {
		PartyRepItem partyRepItem = new PartyRepItem(partyClass.getCanonicalName());

		return new NegotiationPartyInternal(partyRepItem, profileRepItem, session, info, getAgentID(partyClass));
	}

	private AgentID getAgentID(Class<? extends NegotiationParty> partyClass) {
		return new AgentID(partyClass.getName());
	}
}
