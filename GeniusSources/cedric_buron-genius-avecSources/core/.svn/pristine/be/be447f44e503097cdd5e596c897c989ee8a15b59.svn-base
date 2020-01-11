package negotiator.gui.boaparties;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import negotiator.boaframework.BOA;
import negotiator.boaframework.BoaType;
import negotiator.exceptions.InstantiateException;
import negotiator.repository.RepositoryFactory;
import negotiator.repository.boa.BoaRepItem;
import negotiator.repository.boa.BoaRepItemList;
import negotiator.repository.boa.BoaRepository;
import negotiator.repository.boa.BoaWithSettingsRepItem;
import negotiator.repository.boa.ParameterList;
import panels.SingleSelectionModel;

/**
 * Contains GUI models for user settings to create a
 * {@link BoaWithSettingsRepItem}.
 *
 * @param <T>
 *            the type of {@link BOA} component that this model is manipulating,
 *            and that comes out of the {@link BoaWithSettingsRepItem} that is
 *            delivered.
 */
public class BoaComponentModel<T extends BOA> {

	/**
	 * List of selectable components of the given type.
	 */
	SingleSelectionModel<BoaRepItem<T>> componentsListModel;

	private BoaParametersModel parametersModel = new BoaParametersModel();

	/**
	 * Creates a model from given existing settings.
	 * 
	 * @param existingItem
	 *            the existing settings. Type must match T.
	 * @throws InstantiateException
	 *             if problem with repo
	 */
	public BoaComponentModel(final BoaWithSettingsRepItem<T> existingItem) {
		BoaType type = existingItem.getBoa().getType();
		loadComponents(type);
		componentsListModel.setSelectedItem(existingItem.getBoa());
		connect();

	}

	/**
	 * Connects listener to ensure {@link #refreshParams()} is called when
	 * something changes. Also calls {@link #refreshParams()} a first time.
	 */
	private void connect() {
		refreshParams();

		componentsListModel.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				refreshParams();
			}
		});

	}

	/**
	 * set the parameters to match the currently selected component. ASSUMES
	 * current selection is valid.
	 */
	private void refreshParams() {
		try {
			parametersModel.setParameters(componentsListModel.getSelection().getInstance().getParameterSpec());
		} catch (InstantiateException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Construct model with default settings for given type.
	 * 
	 * @param type
	 *            the type, must match T.
	 */
	public BoaComponentModel(BoaType type) {
		loadComponents(type);
		// select first, so that we always have a proper selection (for
		// #refreshParams)
		componentsListModel.setSelectedItem(componentsListModel.getAllItems().get(0));
		connect();
	}

	/**
	 * Load all alternative components for given type
	 * 
	 * @param type
	 *            the {@link BoaType} that this model is dealing with. Should
	 *            match T.
	 */
	private void loadComponents(BoaType type) {
		if (type == BoaType.UNKNOWN || type == null) {
			throw new IllegalArgumentException("unsupported type=" + type);
		}
		BoaRepItemList<BoaRepItem<T>> possibleComponents = getBoaRepo().getBoaComponents(type);
		this.componentsListModel = new SingleSelectionModel<BoaRepItem<T>>(possibleComponents);
	}

	/**
	 * Factory method, for testing.
	 * 
	 * @return boa repository
	 */
	protected BoaRepository getBoaRepo() {
		return RepositoryFactory.getBoaRepository();
	}

	/**
	 * all available settings.
	 */
	public List<BoaWithSettingsRepItem<T>> getValues() {
		List<BoaWithSettingsRepItem<T>> list = new ArrayList<>();
		for (ParameterList setting : parametersModel.getSettings()) {
			list.add(new BoaWithSettingsRepItem<T>(componentsListModel.getSelection(), setting));
		}
		return list;
	}

	public SingleSelectionModel<BoaRepItem<T>> getComponentsListModel() {
		return componentsListModel;
	}

	/**
	 * @return the {@link BoaParametersModel}.
	 */
	public BoaParametersModel getParameters() {
		return parametersModel;
	}

}
