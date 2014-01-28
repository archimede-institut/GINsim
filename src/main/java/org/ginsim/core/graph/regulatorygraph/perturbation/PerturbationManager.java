package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;


/**
 * Save/open perturbation definitions along with the model.
 */
public class PerturbationManager extends BasicGraphAssociatedManager {

	public static final String KEY = "mutant";
	
	public PerturbationManager() {
		super(KEY, new String[] {"perturbation"});
	}
	
    public Object doOpen(InputStream is, Graph graph)  throws GsException{
    	
        PerturbationParser parser = new PerturbationParser((RegulatoryGraph) graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
    	
        ListOfPerturbations lMutant = (ListOfPerturbations) getObject(graph);
        try {
            XMLWriter out = new XMLWriter(os, null);
            lMutant.toXML(out);
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    @Override
	public Object doCreate( Graph graph) {
    	if (graph instanceof RegulatoryGraph) {
    		return new ListOfPerturbations((RegulatoryGraph)graph);
    	}
    	return null;
	}
}
