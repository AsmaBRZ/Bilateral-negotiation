package negotiator.gui.boaparties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.junit.Test;

import negotiator.boaframework.BOAparameter;
import negotiator.exceptions.InstantiateException;
import panels.SingleSelectionModel;

/**
 * bit hacky test. Just opens the panel with a mocked component.
 */
public class BoaComponentPanelTest {
	@Test
	public void testOpenPanel() throws InstantiateException, InterruptedException {
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		// Mock a Boa component model to test the GUI
		BoaComponentModel model = mock(BoaComponentModel.class);
		SingleSelectionModel componentlist = mock(SingleSelectionModel.class);
		when(model.getComponentsListModel()).thenReturn(componentlist);
		BoaParametersModel params = mock(BoaParametersModel.class);
		when(model.getParameters()).thenReturn(params);
		when(params.getSetting()).thenReturn(new ArrayList<BOAparameter>());

		List<String> allitems = Arrays.asList(new String[] { "comp1", "comp2", "comp3" });
		when(componentlist.getAllItems()).thenReturn(allitems);
		when(componentlist.getSelectedItem()).thenReturn(allitems.get(0));

		frame.getContentPane().add(new BoaComponentPanel(model, "test boa component panel"), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(2000);
		frame.setVisible(false);
	}

}
