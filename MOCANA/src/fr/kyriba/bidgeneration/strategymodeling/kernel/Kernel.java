package fr.kyriba.bidgeneration.strategymodeling.kernel;

import fr.kyriba.history.HistoryHandler;

/**
 * P-CBU created on 13/06/17.
 */
public interface Kernel {
    /**Abstraction du kernel*/
    double apply (Double x, Double y);

    void optimize(HistoryHandler handler);

    double gradient(double x, double y);
}
