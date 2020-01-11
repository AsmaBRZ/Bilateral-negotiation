package negotiator.parties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiatorException;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.DomainRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.timeline.Timeline;

public class NegotiationPartyInternalTest {
	@Test
	public void createParty() throws InstantiateException, RepositoryException, NegotiatorException, IOException {
		PartyRepItem partyRepItem = new PartyRepItem("agents.nastyagent.NullBid");
		DomainRepItem domain = new DomainRepItem(new URL("file:test/resources/partydomain/party_domain.xml"));
		ProfileRepItem profileRepItem = new ProfileRepItem(
				new URL("file:test/resources/partydomain/party1_utility.xml"), domain);

		Session session = mock(Session.class);
		Timeline timeline = mock(Timeline.class);
		Deadline deadline = mock(Deadline.class);
		when(session.getTimeline()).thenReturn(timeline);
		when(session.getDeadlines()).thenReturn(deadline);
		SessionsInfo info = new SessionsInfo(null, PersistentDataType.DISABLED, true);
		new NegotiationPartyInternal(partyRepItem, profileRepItem, session, info, new AgentID("testname"));

	}

	@Test(expected = NullPointerException.class)
	public void createPartyWithNullID()
			throws InstantiateException, RepositoryException, NegotiatorException, IOException {
		PartyRepItem partyRepItem = new PartyRepItem("agents.nastyagent.NullBid");
		DomainRepItem domain = new DomainRepItem(new URL("file:test/resources/partydomain/party_domain.xml"));
		ProfileRepItem profileRepItem = new ProfileRepItem(
				new URL("file:test/resources/partydomain/party1_utility.xml"), domain);

		Session session = mock(Session.class);
		SessionsInfo info = new SessionsInfo(null, PersistentDataType.DISABLED, true);
		new NegotiationPartyInternal(partyRepItem, profileRepItem, session, info, null);

	}

}
