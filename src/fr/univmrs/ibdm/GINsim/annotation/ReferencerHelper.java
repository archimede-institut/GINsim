package fr.univmrs.ibdm.GINsim.annotation;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;

public class ReferencerHelper implements AnnotationHelper {
	Map m_ref = new HashMap();
	URLEncoder uenc;
	boolean ignore = false;
	Vector v_bibFile = new Vector();

	//TODO: save/restore the added biblio files
	
	public void addFile() {
		JFileChooser jfc = new JFileChooser();
		int r = jfc.showOpenDialog(null);
		if (r != JFileChooser.APPROVE_OPTION) {
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		v_bibFile.add(fileName);
		ReferencerParser parser = new ReferencerParser();
		parser.go(m_ref, fileName);
	}
	
	public void ignore() {
		ignore = true;
	}
	
	public void update(AnnotationLink l, GsGraph graph) {
		if (!ignore && !m_ref.containsKey(l.value)) {
			if (graph != null) {
				GsGraphNotificationAction action = new GsGraphNotificationAction() {
					String[] t = {Translator.getString("STR_addBib"), Translator.getString("STR_ignore")};
					public boolean timeout(GsGraph graph, Object data) {
						return true;
					}
				
					public boolean perform(GsGraph graph, Object data, int index) {
						switch (index) {
							case 0:
								((ReferencerHelper)data).addFile();
								break;
							case 1:
								((ReferencerHelper)data).ignore();
								break;
						}
						return true;
					}
				
					public String[] getActionName() {
						return t;
					}
				
				};
				
				graph.addNotificationMessage(new GsGraphNotificationMessage(graph,
						Translator.getString("STR_noref"), 
					action, this,
					GsGraphNotificationMessage.NOTIFICATION_WARNING));
			} else {
				// TODO: finish biblio manager
				// System.out.println("got problem...");
			}
		}
	}

	public void open(AnnotationLink l) {
		Ref ref = (Ref)m_ref.get(l.value);
		if (ref != null) {
			ref.open();
		}
	}

	public static void setup() {
		ReferencerHelper h = new ReferencerHelper();
		AnnotationLink.addHelperClass("ref", h);
	}
}

class Ref {
	String key;
	String pdf;
	String doi;
	
	public void open() {
		if (pdf != null && Tools.openFile(pdf)) {
			return;
		}
		Tools.webBrowse(HttpHelper.DOIBASE+doi);
	}
}

class ReferencerParser extends GsXMLHelper {

	Map m_ref;
	Ref curRef;
	String baseDir;
    /**
     * @param graph expected node order
     */
    public void go(Map m_ref, String filename) {
    	this.m_ref = m_ref;
    	File f = new File(filename);
    	if (!f.exists()) {
    		return;
    	}
    	baseDir = f.getParent()+"/";
    	startParsing(f, false);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if ("doc".equals(qName)) {
        	curRef = new Ref();
        } else if ("key".equals(qName)) {
        	curval = "";
        } else if ("bib_doi".equals(qName)) {
        	curval = "";
        } else if ("relative_filename".equals(qName)) {
        	curval = "";
        }
    }
    
    public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if ("doc".equals(qName)) {
        	if (curRef != null && curRef.key != null) {
        		m_ref.put(curRef.key, curRef);
        	}
        	curRef = null;
        	curval = null;
        } else if ("key".equals(qName)) {
        	if (curRef != null) {
        		curRef.key = curval;
        	}
        	curval = null;
        } else if ("bib_doi".equals(qName)) {
        	if (curRef != null) {
        		curRef.doi = curval;
        	}
        	curval = null;
        } else if ("relative_filename".equals(qName)) {
        	if (curRef != null) {
        		try {
					curval = URLDecoder.decode(curval, "UTF-8");
	        		File f = new File(baseDir+curval);
	        		if (f.exists()) {
	        			curRef.pdf = f.getAbsolutePath();
	        			return;
	        		}
				} catch (UnsupportedEncodingException e) {}
    			System.out.println("pdf missed: "+curval);
        	}
        	curval = null;
        }
	}

	public GsGraph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }
}