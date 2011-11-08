package fr.univmrs.tagc.GINsim.annotation;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Graph;
import org.xml.sax.Attributes;

import fr.univmrs.tagc.GINsim.graph.BasicGraphAssociatedManager;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.xml.XMLHelper;


public class BiblioManager extends BasicGraphAssociatedManager {

	public BiblioManager() {
		this.key = "biblio";
		AnnotationLink.addHelperClass("ref", key);
	}
	
	public Object doCreate( Graph graph) {
		
		return new BiblioList( graph);
	}

	public Object doOpen(InputStream is, Graph graph) {
		
		BiblioList bibList = (BiblioList) graph.getObject(key, true);
		BiblioParser parser = new BiblioParser(bibList);
		parser.startParsing(is, false);
		return bibList;
	}
}

class BiblioParser extends XMLHelper {

	String baseDir;
	BiblioList bibList;
	
	static final int FILE = 0;
	static final int REF = 1;
	static final int LINK = 2;

	static Map CALLMAP = new TreeMap();
	static {
		addCall("file", FILE, CALLMAP, STARTONLY, false);
		addCall("ref", REF, CALLMAP, STARTONLY, true);
		addCall("link", LINK, CALLMAP, STARTONLY, true);
	}
	
    /**
     * @param graph expected node order
     */
    public BiblioParser(BiblioList bibList) {
    	this.bibList = bibList;
    	this.m_call = CALLMAP;
    }
    
	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case FILE:
				bibList.addFile(attributes.getValue("filename"));
				break;
			case REF:
				bibList.addRef(attributes.getValue("key"));
				break;
			case LINK:
				bibList.addLinkToCurRef(attributes.getValue("key"), attributes.getValue("value"));
				break;
		}
	}
}
