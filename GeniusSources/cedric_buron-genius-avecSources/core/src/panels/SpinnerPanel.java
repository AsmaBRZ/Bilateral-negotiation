package panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

/**
 * Spinner but with text label.
 *
 */
@SuppressWarnings("serial")
public class SpinnerPanel extends JPanel {

	public SpinnerPanel(String label, SpinnerModel model) {
		setLayout(new BorderLayout());
		add(new JLabel(label), BorderLayout.WEST);
		JSpinner spinner = new JSpinner(model);
		spinner.setMaximumSize(new Dimension(300, 30));
		add(spinner, BorderLayout.CENTER);
		// aligns the RIGHT side of the panel with the center of the parent.
		// This limits the total width
		setAlignmentX(Component.RIGHT_ALIGNMENT);
		setMaximumSize(new Dimension(3000000, 30));
	}

}
