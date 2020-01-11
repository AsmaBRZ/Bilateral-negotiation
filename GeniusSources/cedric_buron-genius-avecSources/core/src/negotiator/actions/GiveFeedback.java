package negotiator.actions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import negotiator.AgentID;
import negotiator.Feedback;
import negotiator.protocol.MediatorFeedbackBasedProtocol;

/**
 * An agent can give feedback on other actions using this action. This action is
 * used with the {@link MediatorFeedbackBasedProtocol}.
 * 
 * @author Reyhan
 */

@XmlRootElement
public class GiveFeedback extends DefaultAction {

	@XmlElement
	protected Feedback feedback;

	public GiveFeedback(AgentID party, Feedback feedback) {
		super(party);
		this.feedback = feedback;
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public String toString() {
		return "Feedback: " + (feedback == null ? "null"
				: (feedback == Feedback.BETTER ? "Better" : (feedback == Feedback.SAME ? "SAME" : "Worse")));
	}

}
