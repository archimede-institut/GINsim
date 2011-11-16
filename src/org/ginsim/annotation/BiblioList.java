package org.ginsim.annotation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageAction;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphListener;
import org.xml.sax.Attributes;

import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.common.OpenHelper;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLHelper;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;


public class BiblioList implements XMLize, OpenHelper, GraphListener {

	private final Map<String, Date> files = new TreeMap<String, Date>();
	private final Map<String, Ref> m_references = new HashMap<String, Ref>();
	private final Set<String> m_used = new HashSet<String>();
	private final Graph<?,?> graph;

	private Ref curRef = null;
	private boolean parsing;

	public BiblioList( Graph<?,?> graph, boolean parsing) {
		this.graph = graph;
		this.parsing = parsing;
		graph.addGraphListener(this);
	}
	/**
	 * FIXME: don't use this constructor
	 * @param graph
	 */
	public BiblioList( Graph graph) {
		this(graph, false);
	}

	public void toXML(XMLWriter out, Object param, int mode) throws IOException {
		out.openTag("biblio");
		
		out.openTag("files");
		for (String key: files.keySet()) {
			out.addTag("file", new String[] {"filename", key});
		}
		out.closeTag();
		
		out.openTag("refs");
		for (String key: m_used) {
			Ref ref = m_references.get(key);
			if (ref != null) {
				ref.toXML(out, param, mode);
			}
		}
		out.closeTag();
		
		out.closeTag();
	}

	public void addRef(String key) {
		curRef = new Ref();
		setKey(key);
	}
	
	public void setKey(String key) {
		if (key == null || curRef == null || curRef.key != null) {
			return;
		}
		curRef.key = key;
		m_references.put(key, curRef);
	}
	
	public void addLinkToCurRef(String proto, String value) {
		if (curRef == null) {
			System.out.println("no current ref");
			return;
		}
		curRef.addLink(proto, value);
	}

	public void add(String proto, String value) {
		if (!proto.equals("ref")) {
			return;
		}
		m_used.add(value);
		if (!m_references.containsKey(value)) {
			addMissingRefWarning(value);
		}
	}

	protected void addFile() {
		JFileChooser jfc = new JFileChooser();
		int r = jfc.showOpenDialog(null);
		if (r != JFileChooser.APPROVE_OPTION) {
			return;
		}
		String fileName = jfc.getSelectedFile().getAbsolutePath();
		addFile(fileName);
	}

	public boolean open(String proto, String value) {
		if (!proto.equals("ref")) {
			return false;
		}
		Ref ref = (Ref)m_references.get(value);
		if (ref == null) {
			addMissingRefWarning(value);
			return false;
		}
		ref.open();
		return true;
	}

	@Override
	public void endParsing() {
		parsing = false;
		for (String key: m_used) {
			if (!m_references.containsKey(key)) {
				addMissingRefWarning(key);
				break;
			}
		}
	}
	public void addMissingRefWarning(String value) {
		if (parsing) {
			return;
		}
		// just in case: check if one of the source file has been updated
		Iterator it = files.entrySet().iterator();
		while (it.hasNext()) {
			Entry e = (Entry)it.next();
			Date d = (Date)e.getValue();
			if (d != null) {
				File f = new File((String)e.getKey());
				if (f.lastModified() > d.getTime()) {
					addFile(f.getAbsolutePath());
					if (m_references.containsKey(value)) {
						return;
					}
				}
			}
		}
		NotificationMessageAction action = new NotificationMessageAction() {
			String[] t = {Translator.getString("STR_addBib"), Translator.getString("STR_ignore")};
			public boolean timeout(NotificationMessageHolder graph, Object data) {
				return true;
			}
		
			public boolean perform( NotificationMessageHolder graph, Object data, int index) {
				switch (index) {
					case 0:
						((BiblioList)data).addFile();
						break;
					case 1:
						//((BiblioList)data).ignore();
						break;
				}
				return true;
			}
		
			public String[] getActionName() {
				return t;
			}
		
		};
		
		new NotificationMessage((NotificationMessageHolder)graph, Translator.getString("STR_noref"), action, this, NotificationMessage.NOTIFICATION_WARNING);
	}
	
	public String getLink(String proto, String value) {
		if (!proto.equals("ref")) {
			return null;
		}
		Ref ref = (Ref)m_references.get(value);
		if (ref == null) {
			return null;
		}
		// quick hack: take the first ignoring "file"
		Iterator it = ref.links.entrySet().iterator();
		while (it.hasNext()) {
			Entry e = (Entry)it.next();
			if (!"file".equals(e.getKey())) {
				return Tools.getLink(e.getKey(), e.getValue());
			}
		}
		return null;
	}

	public void addFile(String fileName) {
		files.put(fileName, new Date());
		
		File f = new File(fileName);
		if (f.exists()) {
			// FIXME: add a proper mechanism choose the parser
			if (fileName.endsWith(".bib")) {
				new BibTexParser(this,fileName);
			} else {
				new ReferencerParser(this, fileName);
			}
			endParsing();
		} else {
			NotificationMessageAction action = new NotificationMessageAction() {
				String[] t = {Translator.getString("STR_purge")};
				public boolean timeout(NotificationMessageHolder graph, Object data) {
					return true;
				}
			
				public boolean perform(NotificationMessageHolder graph, Object data, int index) {
					((BiblioList)((Object[])data)[0]).removeFile(((Object[])data)[1].toString());
					return true;
				}
			
				public String[] getActionName() {
					return t;
				}
			
			};
			
			new NotificationMessage((NotificationMessageHolder)graph, Translator.getString("STR_noBibFile"),
					action, new Object[] {this, fileName}, NotificationMessage.NOTIFICATION_WARNING);
		}
	}
	
	public void removeFile(String fileName) {
		files.remove(fileName);
	}

	public GsGraphEventCascade edgeAdded(Edge data) {
		return null;
	}

	public GsGraphEventCascade edgeRemoved(Edge data) {
		return null;
	}

	public GsGraphEventCascade edgeUpdated(Edge data) {
		return null;
	}

	public GsGraphEventCascade graphMerged(Collection data) {
		return null;
	}

	public GsGraphEventCascade vertexAdded(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexUpdated(Object data) {
		return null;
	}
}

class Ref {
	String key;
	Map links = new TreeMap();
	
	public void addLink(String proto, String value) {
		links.put(proto, value);
	}

	public void open() {
		if (links.containsKey("file") && Tools.open("file", links.get("file"))) {
			return;
		}
		Iterator it = links.entrySet().iterator();
		while (it.hasNext()) {
			Entry e = (Entry)it.next();
			if (Tools.open(e.getKey(), e.getValue())) {
				return;
			}
		}
	}
	public void toXML(XMLWriter out, Object param, int mode) throws IOException {
		out.openTag("ref");
		out.addAttr("key", key);
		for (Iterator it=links.entrySet().iterator() ; it.hasNext() ; ) {
			Entry e = (Entry)it.next();
			out.addTag("link", new String[] {"key", e.getKey().toString(), "value", e.getValue().toString()});
		}
		out.closeTag();
	}
}


class ReferencerParser extends XMLHelper {

	String baseDir;
	BiblioList bibList;
	
	String extra = null;
	
	static final int DOC = 0;
	static final int KEY = 1;
	static final int DOI = 2;
	static final int EXTRA = 3;
	static final int FILENAME = 4;

	static Map CALLMAP = new TreeMap();
	static {
		addCall("doc", DOC, CALLMAP, STARTONLY, false);
		addCall("key", KEY, CALLMAP, ENDONLY, true);
		addCall("bib_doi", DOI, CALLMAP, ENDONLY, true);
		addCall("bib_extra", EXTRA, CALLMAP, BOTH, true);
		addCall("relative_filename", FILENAME, CALLMAP, ENDONLY, true);
		addCall("filename", FILENAME, CALLMAP, ENDONLY, true);
	}
	
    /**
     * @param graph expected node order
     */
    public ReferencerParser(BiblioList bibList, String path) {
    	this.bibList = bibList;
    	this.m_call = CALLMAP;
    	
		try {
			File f = new File(path);
			baseDir = f.getParent();
			startParsing(Tools.getStreamForPath(path), false);
		} catch (Exception e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
    }
    
	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case DOC:
				bibList.addRef(null);
				break;
			case EXTRA:
				extra = attributes.getValue("key");
				break;
		}
	}

	protected void endElement(int id) {
		switch (id) {
			case KEY:
				bibList.setKey(curval);
				break;
			case DOI:
				if (curval.trim().length() > 0) {
					bibList.addLinkToCurRef("doi", curval);
				}
				break;
			case EXTRA:
				if (curval.trim().length() > 0) {
					if ("pmid".equals(extra)) {
						bibList.addLinkToCurRef("pubmed", curval);
					}
				}
				break;
			case FILENAME:
				if (curval.startsWith("file://localhost")) {
					curval = curval.substring(16);
				} else if (curval.startsWith("file:")) {
					curval = curval.substring(5);
				} else {
					curval = baseDir+File.separator+curval;
				}
				
				try {
					curval = URLDecoder.decode(curval, "utf8");
				} catch (UnsupportedEncodingException e) {
				}
				File f = new File(curval);
				if (f.exists()) {
					bibList.addLinkToCurRef("file", curval);
				} else {
					System.out.println("  could not find file "+curval);
				}
				break;
		}
	}
}

class BibTexParser {
	String baseDir;
	
	static final Map m_bibtextoginsim = new TreeMap();
	static {
		m_bibtextoginsim.put("doi", "doi");
		m_bibtextoginsim.put("pmid", "pubmed");
		m_bibtextoginsim.put("pubmed", "pubmed");
		m_bibtextoginsim.put("pdf", "file");
		m_bibtextoginsim.put("ps", "file");
		m_bibtextoginsim.put("local-url", "file");
	}

	public BibTexParser(BiblioList biblist, String path) {
		try {
			File f = new File(path);
			baseDir = f.getParent();
			
			BibtexFile bibtexFile = new BibtexFile();
			BibtexParser parser = new BibtexParser(false);
			parser.parse(bibtexFile, new FileReader(f));
			
			Iterator it = bibtexFile.getEntries().iterator();
			while (it.hasNext()) {
				Object next = it.next();
				if (next instanceof BibtexEntry) {
					BibtexEntry entry = (BibtexEntry)next;
					biblist.addRef(entry.getEntryKey());
					Iterator it_links = m_bibtextoginsim.entrySet().iterator();
					while (it_links.hasNext()) {
						Entry e = (Entry)it_links.next();
						String k = (String)e.getKey();
						BibtexAbstractValue v = entry.getFieldValue(k);
						if (v != null) {
							biblist.addLinkToCurRef((String)e.getValue(), v.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
	}
}
