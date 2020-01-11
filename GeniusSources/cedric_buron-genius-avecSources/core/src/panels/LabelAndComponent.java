package panels;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel with a text on the left and an arbitrary component on the right
 *
 */
@SuppressWarnings("serial")
public class LabelAndComponent extends JPanel {
	public LabelAndComponent(String text, Component comp) {
		super(new BorderLayout());
		add(new JLabel(text), BorderLayout.WEST);
		add(comp, BorderLayout.CENTER);
	}
}
