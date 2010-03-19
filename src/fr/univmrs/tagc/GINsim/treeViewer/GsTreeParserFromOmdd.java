package fr.univmrs.tagc.GINsim.treeViewer;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsTreeParserFromOmdd extends GsTreeParser {

	
	
	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void parseOmdd() {
		System.out.println("Not implemented yet");
	}

	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an omdd, in one pass
	 * @param initialVertex
	 */
	public void initRealDepth(OmddNode omdd) {
		_initRealDepth(omdd);
	}
	private void _initRealDepth(OmddNode omdd) {
		//TODO
	}

	public void setSource(Object source) {
		// TODO Auto-generated method stub
		
	}

	public void updateLayout(GsTreeNode vertex) {
		// TODO Auto-generated method stub
		
	}

	public void updateLayout(GsVertexAttributesReader vreader, GsTreeNode vertex) {
		// TODO Auto-generated method stub
		
	}
	
}