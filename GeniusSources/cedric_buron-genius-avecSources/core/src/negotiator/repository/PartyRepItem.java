package negotiator.repository;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.Global;
import negotiator.exceptions.InstantiateException;
import negotiator.parties.Mediator;
import negotiator.parties.NegotiationParty;

/**
 * This repository item contains all info about a {@link NegotiationParty} that
 * can be loaded. immutable.
 */

@SuppressWarnings("serial")
@XmlRootElement(name = "party")
public class PartyRepItem extends ParticipantRepItem {

	private final static String FAILED = "FAILED TO LOAD";
	/**
	 * This can be two things:
	 * <ul>
	 * <li>a class path, eg "agents.anac.y2010.AgentFSEGA.AgentFSEGA". In this
	 * case, the agent must be on the class path to load.
	 * <li>a full path, eg
	 * "/Volumes/documents/NegoWorkspace3/NegotiatorGUI/src/agents/anac/y2010/AgentFSEGA/AgentFSEGA.java"
	 * . In this case, we can figure out the class path ourselves and load it.
	 * </ul>
	 */
	@XmlAttribute
	protected String classPath = "";

	/**
	 * Name, also a short version of the class path.
	 */
	private String partyName = FAILED;

	/**
	 * description of this agent. Cached, Not saved to XML.
	 */
	private String description = FAILED;

	/**
	 * True if this party is a mediator.
	 */
	private Boolean isMediator = false;

	/**
	 * needed to support XML de-serialization.
	 * 
	 * @throws InstantiateException
	 */
	@SuppressWarnings("unused")
	private PartyRepItem() throws InstantiateException {
	}

	/**
	 * 
	 * @param path
	 *            full.path.to.class or file name.
	 */
	public PartyRepItem(String path) {
		if (path == null) {
			throw new NullPointerException(path = null);
		}
		classPath = path;
	}

	/**
	 * Init our fields to cache the party information. party must have been set
	 * before getting here.
	 * 
	 * @param party
	 * @throws InstantiateException
	 */
	@Override
	protected NegotiationParty init() throws InstantiateException {
		NegotiationParty party1 = super.init();
		partyName = party1.getClass().getSimpleName();
		description = party1.getDescription();
		isMediator = party1 instanceof Mediator;
		return party1;
	}

	/**
	 * @return classpath, either as full package class path (with dots) or as an
	 *         absolute path to a .class file.
	 */
	public String getClassPath() {
		return classPath;
	}

	public String getName() {
		initSilent();
		return partyName;
	}

	public String getDescription() {
		initSilent();
		return description;
	}

	public String toString() {
		initSilent();
		return "PartyRepositoryItem[" + partyName + "," + classPath + "," + description + ", is mediator="
				+ isMediator().toString() + "]";
	}

	public Boolean isMediator() {
		initSilent();
		return isMediator;
	}

	public NegotiationParty load() throws InstantiateException {
		return (NegotiationParty) Global.loadObject(classPath);

	}

	/**
	 * @return true if partyName and classPath equal. Note that partyName alone
	 *         is sufficient to be equal as keys are unique.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PartyRepItem))
			return false;
		return classPath.equals(((PartyRepItem) o).classPath);
	}

	@Override
	public String getUniqueName() {
		return Global.shortNameOfClass(classPath);
	}
}