package negotiator.repository.boa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
public class ParameterList extends ArrayList<ParameterRepItem> {

	public ParameterList() {
	}

	public ParameterList(ParameterList parameterList) {
		addAll(parameterList);
	}

	// /**
	// * creates ParameterList by adding, in given order, the given
	// * {@link BOAparameter}s. All these are converted, using only the name and
	// * {@link BOAparameter#getLow()} value.
	// *
	// * @param parameters
	// * the parameters to load into this list.
	// */
	// public ParameterList(ArrayList<BOAparameter> parameters) {
	// for (BOAparameter p : parameters) {
	// add(new ParameterRepItem(p.getName(), p.getLow()));
	// }
	// }

	/**
	 * @return this parameterlist as a hashmap.
	 */
	public Map<String, Double> asMap() {
		Map<String, Double> map = new HashMap<String, Double>();
		for (ParameterRepItem p : this) {
			map.put(p.getName(), p.getValue().doubleValue());
		}
		return map;
	}

	/**
	 * same as add, but returns new list.
	 * 
	 * @param newParam
	 *            the parameter to add
	 * @return new list containing this, plus newParam
	 */
	public ParameterList include(ParameterRepItem newParam) {
		ParameterList list = new ParameterList(this);
		list.add(newParam);
		return list;
	}

}
