package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.MultiplePerturbation;
import org.ginsim.common.xml.XMLWriter;

public class PerturbationMultiple extends MultiplePerturbation<Perturbation> implements Perturbation {

    /**
     * Make sure that the list contains only simple perturbations
     *
     * @param l
     * @return
     */
    private static List<Perturbation> getSimplePerturbations(List<Perturbation> l) {
        List<Perturbation> result = new ArrayList<Perturbation>();
        fillSimplePerturbations(result, l);
        return result;
    }

    private static void fillSimplePerturbations(List<Perturbation> result, List<Perturbation> l) {
        for (Perturbation p: l) {
            if (p instanceof MultiplePerturbation) {
                fillSimplePerturbations(result, ((MultiplePerturbation)p).perturbations);
            } else if(!result.contains(p)) {
                result.add(p);
            }
        }
    }

	/**
	 * Create a multiple perturbation
	 * 
	 * @param perturbations list of perturbations to apply.
	 */
	public PerturbationMultiple(List<Perturbation> perturbations) {
		super(getSimplePerturbations(perturbations));
	}
	
	@Override
	public void toXML(XMLWriter out) throws IOException {
        for (Perturbation p: perturbations) {
            p.toXML(out);
        }
	}

	@Override
	public Perturbation clone(ListOfPerturbations manager, Map<NodeInfo, NodeInfo> m_nodes, Map<Perturbation, Perturbation> m_perturbations) {
		List<Perturbation> newPerturbations = new ArrayList<Perturbation>();
		for (Perturbation perturbation: perturbations) {
			Perturbation newPerturbation = m_perturbations.get(perturbation);
			if (newPerturbation == null) {
				return null;
			}
			newPerturbations.add(newPerturbation);
		}
		
		return manager.addMultiplePerturbation(newPerturbations);
	}
}
