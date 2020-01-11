package negotiator.gui.session;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import negotiator.gui.renderer.RepItemListCellRenderer;
import negotiator.repository.ParticipantRepItem;
import negotiator.repository.ProfileRepItem;
import panels.ComboboxSelectionPanel;
import panels.LabelAndComponent;
import panels.TextPanel;

/**
 * Panel where user can edit a participant settings
 *
 */
@SuppressWarnings("serial")
public class ParticipantPanel extends JPanel {

	private ParticipantModel participantModel;

	public ParticipantPanel(ParticipantModel participantModel) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.participantModel = participantModel;
		init();
	}

	private void init() {
		add(new LabelAndComponent("Party ID", new TextPanel(participantModel.getIdModel())));

		final ComboboxSelectionPanel<ParticipantRepItem> partycombo = new ComboboxSelectionPanel<>("Party Strategy",
				participantModel.getPartyModel());
		partycombo.setCellRenderer(new RepItemListCellRenderer());
		add(partycombo);

		final ComboboxSelectionPanel<ProfileRepItem> profilecombo = new ComboboxSelectionPanel<ProfileRepItem>(
				"Preference Profile", participantModel.getProfileModel());
		profilecombo.setCellRenderer(new RepItemListCellRenderer());
		add(profilecombo);

	}

}
