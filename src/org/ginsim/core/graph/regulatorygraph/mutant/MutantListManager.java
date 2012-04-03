package org.ginsim.core.graph.regulatorygraph.mutant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;


/**
 * Save/open simulation parameters along with the model.
 */
public class MutantListManager extends BasicGraphAssociatedManager {

	public static final String KEY = "mutant";
	
	public MutantListManager() {
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
            for (int i=0 ; i<lMutant.getNbElements(null) ; i++) {
                RegulatoryMutantDef mutant = (RegulatoryMutantDef)lMutant.getElement(null, i);
                mutant.toXML(out);
            }
            out.closeTag();
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    @Override
	public Object doCreate( Graph graph) {
		return new RegulatoryMutants( (RegulatoryGraph)graph);
	}
}
