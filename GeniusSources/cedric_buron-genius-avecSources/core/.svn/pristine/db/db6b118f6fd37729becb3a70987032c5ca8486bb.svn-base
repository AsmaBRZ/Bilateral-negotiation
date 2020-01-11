package negotiator.config;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import negotiator.Deadline;
import negotiator.DeadlineType;
import negotiator.exceptions.InstantiateException;
import negotiator.persistent.PersistentDataType;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.ParticipantRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.boa.BoaPartyRepItem;

/**
 * Try to read n a configuration
 *
 */
public class MultilateralTournamentsConfigurationTest {
	@Test
	public void smokeTest() throws JAXBException {
		File file = new File("test/resources/tournamentconfig.xml");
		MultilateralTournamentsConfiguration.load(file);
	}

	@Test
	public void writeSimpleTournamentConfig() throws InstantiateException {
		MultiPartyProtocolRepItem protocol = new MultiPartyProtocolRepItem();
		Deadline deadline2 = new Deadline(180, DeadlineType.ROUND);
		PartyRepItem mediator = new PartyRepItem("unknown.path");
		List<ParticipantRepItem> parties = new ArrayList<>();

		parties.add(new PartyRepItem("simple.party"));
		parties.add(new BoaPartyRepItem("boaparty"));

		List<ProfileRepItem> profiles = new ArrayList<>();
		List<ParticipantRepItem> partiesB = new ArrayList<>();
		List<ProfileRepItem> profilesB = new ArrayList<>();
		int nrepeats = 1;
		int nparties = 2;
		boolean repeatParties = false;
		boolean isRandomSessionOrder = false;
		PersistentDataType type = PersistentDataType.DISABLED;
		boolean enablePrint = false;
		MultilateralTournamentConfiguration config = new MultilateralTournamentConfiguration(protocol, deadline2,
				mediator, parties, profiles, partiesB, profilesB, nrepeats, nparties, repeatParties,
				isRandomSessionOrder, type, enablePrint);

		ByteOutputStream out = new ByteOutputStream();
		config.save(out);

		String result = new String(out.getBytes());
		System.out.println(result);

		assertFalse("serialization must not contain xmlns:", result.contains("xmlns:"));
		assertFalse("serialization must not contain xsi:", result.contains("xsi:"));

	}
}
