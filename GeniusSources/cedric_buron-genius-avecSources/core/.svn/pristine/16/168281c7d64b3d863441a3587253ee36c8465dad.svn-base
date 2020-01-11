package panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import listener.Listener;

/**
 * Shows a single text line input area.
 */
@SuppressWarnings("serial")
public class TextPanel extends JPanel {
	public TextPanel(final TextModel model) {
		setLayout(new BorderLayout());
		final JTextField textfield = new JTextField(model.getText());
		add(textfield, BorderLayout.CENTER);
		// not working?
		// setMaximumSize(new Dimension(99999999, 30));
		textfield.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// we ignore remove events. They are part of a replacement
				// procedure and we should not copy the in-between empty strings
				// into the model
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				model.setText(textfield.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				model.setText(textfield.getText());
			}
		});

		model.addListener(new Listener<String>() {
			@Override
			public void notifyChange(final String data) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						textfield.setText((String) data);
					}
				});
			}
		});

	}
}
