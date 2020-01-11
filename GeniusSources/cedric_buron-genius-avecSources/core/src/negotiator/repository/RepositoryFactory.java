package negotiator.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import negotiator.repository.boa.BoaRepository;
import negotiator.session.RepositoryException;

/**
 * Factory for Repositories.
 */
public class RepositoryFactory {

	// cache for domain repo.
	private static Repository<DomainRepItem> domainRepos = null;

	// cache for BoaPartyRepository
	private static BoaPartyRepository boapartyrepo = null;

	private static BoaRepository boarepo;

	public static DomainRepItem getDomainByName(String name) throws Exception {
		Repository<DomainRepItem> domRep = get_domain_repos();
		DomainRepItem domainRepItem = null;
		for (RepItem tmp : domRep.getItems()) {
			if (((DomainRepItem) tmp).getURL().toString().equals(name)) {
				domainRepItem = (DomainRepItem) tmp;
				break;
			}
		}
		return domainRepItem;
	}

	public static Repository<DomainRepItem> get_domain_repos(String filename, String sourceFolder)
			throws RepositoryException {
		if (domainRepos == null) {
			domainRepos = Repository.fromFile(filename);
		}
		return domainRepos;
	}

	public static Repository<DomainRepItem> get_domain_repos() throws RepositoryException {
		final String FILENAME = "domainrepository.xml"; // ASSUMPTION there is
														// only one domain
														// repository
		return get_domain_repos(FILENAME, "");

	}

	static ArrayList<DomainRepItem> makedemorepository() throws MalformedURLException {
		ArrayList<DomainRepItem> its = new ArrayList<>();

		// DomainRepItem dri=new DomainRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_domain.xml"));
		DomainRepItem dri = new DomainRepItem(
				new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_domain.xml"));

		// dri.getProfiles().add(new ProfileRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_seller_utility.xml"),dri));
		// dri.getProfiles().add(new ProfileRepItem(new
		// URL("file:/Volumes/Documents/Wouter/Negotiator/NegoWorkspace/Negotiator/etc/templates/laptopdomain/laptop_empty_utility.xml"),dri));
		dri.getProfiles().add(new ProfileRepItem(
				new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_seller_utility.xml"), dri));
		dri.getProfiles().add(new ProfileRepItem(
				new URL("file:H:/Negotiator/negotiator/templates/laptopdomain/laptop_empty_utility.xml"), dri));
		its.add(dri);

		dri = new DomainRepItem(new URL("file:domain2"));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profilec"), dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profiled"), dri));
		dri.getProfiles().add(new ProfileRepItem(new URL("file:profilee"), dri));
		its.add(dri);

		return its;
	}

	static ArrayList<AgentRepItem> init_temp_repository() {
		ArrayList<AgentRepItem> items = new ArrayList<>();
		items.add(new AgentRepItem("Warning: Repository not loaded", "", ""));
		items.add(new AgentRepItem("Please check that Genius can find agentrepository.xml and domainrepository.xml", "",
				""));
		items.add(new AgentRepItem("And make sure /etc/templates is in the same directory", "", ""));
		items.add(new AgentRepItem("BayesianAgent", "agents.BayesianAgent", "simple agent"));
		items.add(new AgentRepItem("UI agent", "agents.UIAgent", "basic UI agent"));
		return items;
	}

	static ArrayList<PartyRepItem> init_temp_repository2() {
		ArrayList<PartyRepItem> items = new ArrayList<>();
		try {
			items.add(new PartyRepItem("negotiator.parties.CounterOfferHumanNegotiationParty"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	static ArrayList<ProtocolRepItem> init_temp_prot_repository() {
		ArrayList<ProtocolRepItem> items = new ArrayList<>();
		items.add(new ProtocolRepItem("Alternating Offers", "negotiator.protocol.AlternatingOffersMetaProtocol",
				"Alternating Offers"));
		items.add(new ProtocolRepItem("Auction", "negotiator.protocol.AuctionMetaProtocol", "Auction"));
		return items;
	}

	static ArrayList<MultiPartyProtocolRepItem> init_temp_multiprot_repository() {
		ArrayList<MultiPartyProtocolRepItem> items = new ArrayList<>();
		items.add(new MultiPartyProtocolRepItem("Simple Mediated Multiparty Protocol",
				"negotiator.multiPartyProtocol.SimpleMediatedMultipartyProtocol", "Simple Mediated Multiparty Protocol",
				null, null));
		return items;
	}

	public static Repository<ProtocolRepItem> getProtocolRepository() throws RepositoryException {
		return Repository.fromFile("protocolrepository.xml");
	}

	public static Repository<MultiPartyProtocolRepItem> getMultiPartyProtocolRepository() throws RepositoryException {
		return Repository.fromFile("multipartyprotocolrepository.xml");

	}

	public static Repository<AgentRepItem> get_agent_repository() throws RepositoryException {
		return Repository.fromFile("agentrepository.xml");

	}

	public static Repository<PartyRepItem> get_party_repository() throws RepositoryException {
		return Repository.fromFile("partyrepository.xml");

	}

	public static BoaPartyRepository getBoaPartyRepository() throws RepositoryException {
		if (boapartyrepo == null) {
			try {
				// FIXME use Repository.fromFile???
				boapartyrepo = new BoaPartyRepository();
			} catch (FileNotFoundException | JAXBException e) {
				throw new RepositoryException("failed to load BoaPartyRepo", e);
			}
		}
		return boapartyrepo;
	}

	public static BoaRepository getBoaRepository() {
		if (boarepo == null) {
			try {
				boarepo = BoaRepository.loadRepository(new File("boarepository.xml"));
			} catch (JAXBException e) {
				throw new RepositoryException("failed to load BoaRepo", e);
			}
		}
		return boarepo;
	}

}
