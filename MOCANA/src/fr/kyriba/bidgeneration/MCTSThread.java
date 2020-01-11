package fr.kyriba.bidgeneration;

import fr.kyriba.ExperimentsConstants;
import fr.kyriba.bidgeneration.MCTS.MCTree;
import fr.kyriba.history.HistoryHandler;
import fr.kyriba.history.HistoryListHandler;
import fr.kyriba.protocol.Offer;
import fr.kyriba.protocol.ProtocolDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * P-CBU created on 03/05/17.
 */
public class MCTSThread implements Runnable {
    private MCTree tree;
    private MCTSBidGenerator gen;
    private final ProtocolDescriptor protocol;
    private int nbSamples;
    private HistoryHandler history;
/**Thread réalisant les tâches sur l'arbre MCTS en parallèle*/
    public MCTSThread(MCTree tree,MCTSBidGenerator gen, ProtocolDescriptor protocol, HistoryHandler history) {
        this.tree = tree;
        this.gen = gen;
        this.protocol = protocol;
        this.history = history;
        nbSamples = ExperimentsConstants.NB_SAMPLES;
    }

    @Override
    public void run() {
        HistoryHandler threadHandler = new HistoryListHandler();
        threadHandler.storeAll(history.getHistory());
        try {
            MCTree myTree = tree;
            MCTSBidGenerator.writer.write("debut\n");
            while (!gen.needsExtension(myTree)) {
                myTree = MCTSBidGenerator.select(myTree);
            }
            try {
                myTree = gen.extend(myTree);
            }
            catch(Exception e){
                throw new Exception("error in extend: " + e);
            }
            List<Double> utilities = new ArrayList<>();
            for (int j = 0; j < protocol.getPlayerNumber(); j++) {
                utilities.add(0.0);
            }
            for (int i = 0; i < nbSamples; i++) {
                long timeSim = new Date().getTime();
                Offer simulated = gen.simulate(myTree);
                Long elapsed =  new Date().getTime() - timeSim;
                for (int j = 0; j < protocol.getPlayerNumber(); j++) {
                    utilities.set(j, gen.getUtilityModelers().get(j).estimateUtility(threadHandler,gen.getTime()).apply(simulated));
                }
            }
            gen.backpropagate(myTree, utilities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
