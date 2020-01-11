package negotiator.gui.session;

import panels.BooleanModel;
import panels.CheckboxPanel;
import panels.VflowPanelWithBorder;

@SuppressWarnings("serial")
public class BilateralOptionsPanel extends VflowPanelWithBorder {

	public BilateralOptionsPanel(BooleanModel utilutilplotModel, BooleanModel showallBidsModel) {
		super("Bilateral options");

		add(new CheckboxPanel("Show Util-Util Graph", utilutilplotModel));
		add(new CheckboxPanel("Show all bids", showallBidsModel));
	}

}
