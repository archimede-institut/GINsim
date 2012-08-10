package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

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
		super(KEY, null);
	}
	
    public Object doOpen(InputStream is, Graph graph)  throws GsException{
    	
        RegulatoryMutantParser parser = new RegulatoryMutantParser((RegulatoryGraph) graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
    	
        RegulatoryMutants lMutant = (RegulatoryMutants) getObject(graph);
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("mutantList");
            for (Perturbation p: lMutant) {
                p.toXML(out);
            }
            out.closeTag();
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    @Override
	public Object doCreate( Graph graph) {
		return new RegulatoryMutants();
	}
}
