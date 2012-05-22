package org.ginsim.core.annotation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.common.utils.OpenUtils;
import org.ginsim.common.xml.CallDescription;
import org.ginsim.common.xml.CallMode;
import org.ginsim.common.xml.XMLHelper;
import org.xml.sax.Attributes;

public class MiriamURNHelper {

	private static boolean init = false;
	
	public static void setup() {
		if (init) {
			return;
		}
		init = true;
		
		// read MIRIAM database list
		parse("miriam.xml");
		
		// add some aliases
		OpenUtils.addHelperClassAlias("pmid", "pubmed");
		OpenUtils.addHelperClassAlias("hugo", "hgnc");
		OpenUtils.addHelperClassAlias("entrez", "entrez.gene");
	}
	
	private static void parse(String filename) {
		LinkDescriptionParser parser = new LinkDescriptionParser();
		try {
			InputStream stream = IOUtils.getStreamForPath(MiriamURNHelper.class.getPackage(), filename);
			parser.startParsing(stream);
		} catch (Exception e) {
			LogManager.error("Could not parse miriam database list");
			LogManager.error(e);
		}
	}
}


class LinkDescriptionParser extends XMLHelper {
	static Map<String, CallDescription> CALLMAP = new HashMap<String, CallDescription>();
	
	static final int DATATYPE=1, NAME=2, DEFINITION=3, RESOURCE=4;
	static final int URI=10, BASEURL=11, DATAURL=12, PATTERN=13;
	
	static {
		addCall("datatype", DATATYPE, CALLMAP);
		addCall("namespace", NAME, CALLMAP, CallMode.ENDONLYREAD);
		addCall("definition", DEFINITION, CALLMAP,CallMode.ENDONLYREAD);
		// addCall("resource", RESOURCE, CALLMAP);
		
		addCall("uri", URI, CALLMAP, CallMode.BOTHREAD);
		addCall("dataResource", BASEURL, CALLMAP, CallMode.ENDONLYREAD);
		addCall("dataEntry", DATAURL, CALLMAP, CallMode.ENDONLYREAD);
	}

	int count = 0;
	int countAll = 0;
	
	// collected data
	String name, definition;
	String urn, baseurl, entryurl;
	
	protected LinkDescriptionParser() {
		this.m_call = CALLMAP;
	}

	@Override
	protected void startElement(int id, Attributes attributes) {
		switch(id) {
		case DATATYPE:
			// only if not obsolete
			countAll++;
			String obsolete = attributes.getValue("obsolete");
			if (obsolete == null || obsolete.equals("false")) {
				count++;
			}
			break;
		case URI:
//			if (curRow != null && "URN".equals(attributes.getValue("type"))) {
//				readURI = true;
//			}
			break;
		}
	}
	
	@Override
	protected void endElement(int id) {
		switch(id) {
		case DATATYPE:
			new DatabaseInfo(name, definition);
			break;
		case NAME:
			name = curval.trim();
			break;
		case DEFINITION:
			definition = curval.trim();
			break;
		case URI:
			urn = curval.trim();
			break;
		case BASEURL:
			if (baseurl == null) {
				baseurl = curval.trim();
			}
			break;
		case DATAURL:
			if (entryurl == null) {
				entryurl = curval.trim();
			}
			break;
		}
	}
}
