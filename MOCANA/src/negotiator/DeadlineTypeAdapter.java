package negotiator;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * P-CBU created on 28/08/17.
 */
public class DeadlineTypeAdapter extends XmlAdapter<String, DeadlineType> {
    @Override
    public DeadlineType unmarshal(String v) throws Exception {
        if(v.equals("Round")){
            return DeadlineType.ROUND;
        }
        return DeadlineType.TIME;
    }

    @Override
    public String marshal(DeadlineType v) throws Exception {
        if(v.equals(DeadlineType.ROUND)){
            return "Round";
        }
        return "Time";
    }
}
