package org.ginsim.common.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.common.utils.Translator;
import org.ginsim.core.exception.GsException;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



abstract public class XMLHelper extends DefaultHandler implements EntityResolver {

	protected static final int NOCALL = 0;
	protected static final int ENDONLY = 1;
	protected static final int STARTONLY = 2;
	protected static final int BOTH = 3;
	
	static Map m_entities = new TreeMap();
	
	public static void addEntity(String url, String path) {
		m_entities.put(url, path);
	}
	
	protected static void addCall(String tag, int id, int constraint, Map m_call, 
			int callmode, boolean readcontent) {
		CallDescription cd = new CallDescription(id, constraint, callmode, readcontent);
		CallDescription cur = (CallDescription)m_call.get(tag);
		if (cur == null) {
			m_call.put(tag, cd);
		} else {
			cur.other = cd;
		}
	}
	protected static void addCall(String tag, int id, Map m_call, 
			int callmode, boolean readcontent) {
		addCall(tag, id, -1, m_call, callmode, readcontent);
	}
	protected static void addCall(String tag, int id, Map m_call) {
		addCall(tag, id, -1, m_call, BOTH, false);
	}

    /** if set to other than null, characters will be stored here */
	protected String curval;
	protected String s_dtd;
	protected String s_filename;
	
	protected XMLReader xr;
	private boolean showError = true;
	
	protected Map m_call = null;
	
	public void error(SAXParseException e) throws SAXException {
	    //FIXME: once logical functions, input and patterns become part of the DTD, remove this hack
        if (e.getMessage().startsWith("Element type \"value\"")) {
            showError = false;
        }
        if (e.getMessage().startsWith("Attribute \"input\"") || e.getMessage().startsWith("Attribute \"pattern\"")) {
            showError = false;
        }
	    if (!showError) {
	        return;
	    }
        Object[] t = {Translator.getString("STR_stop"), Translator.getString("STR_goOn"), Translator.getString("STR_ignoreNext")};
	    int ret = JOptionPane.showOptionDialog(null, 
                Translator.getString("STR_errorOccured")+":\n\n"+
	            e.getLocalizedMessage()+"\n"
				+ "\nline: "+e.getLineNumber()
				+ "\ncolumn: "+e.getColumnNumber()+"\n"+
                Translator.getString("STR_wantToStop_title"),
                Translator.getString("STR_wantToStop"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, t, t[0]);
	    switch (ret) {
	    	case 0:
	    	    showError = false;
    	    throw e;
	    	case 1:
	    	    break;
	    	case 2:
	    	    showError = false;
	    	    break;
	    }
	}

	public void warning(SAXParseException e) throws SAXException {
		
		GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_NORMAL, e.getLocalizedMessage()
				+ "\nline: "+e.getLineNumber()
				+ "\ncolumn: "+e.getColumnNumber()), null);
	}

    /**
     * if <code>curVal</code> is not null, read characters will be appended there.
     * a bit less work for other implementors, they just have to set curVal to null or ""
     * when they need it.
     * 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (curval != null) {
            curval += new String(ch, start, length);
        }
        super.characters(ch, start, length);
        
    }

    /**
     * start parsing a file.
     * @param file
     * @param b
     */
    public void startParsing(File file, boolean b) {
        try {
            startParsing(new FileInputStream(file), b);
        } catch (FileNotFoundException e) {
        	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e), null);
        }
    }

    /**
     * start parsing a file.
     * @param file
     */
    public void startParsing(File file) {
        startParsing(file, true);
    }

    /**
     * @param is
     */
    public void startParsing(InputStream is) {
        startParsing(is, true);
    }
    /**
     * @param is
     * @param validating 
     */
    public void startParsing(InputStream is, boolean validating) {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(validating);
		try {
			SAXParser sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.setEntityResolver(this);
			xr.setErrorHandler(this);
			xr.parse(new InputSource(new InputStreamReader(is, "UTF-8")));
		} catch (FileNotFoundException e) { 
		    GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (IOException e) {
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (ParserConfigurationException e) {
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (SAXParseException e) {
			GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        } catch (SAXException e) {
        	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    /**
     * do the "entity resolver" job, a bit lazy, this needs some cleanups to become 
     * more generalist, but it does the job for GINsim...
     * 
     * @param publicId
     * @param systemId
     * @return an input source for the DTD; or null to use the default
     * @throws SAXException
     */
	public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException {
		String path = (String)m_entities.get(systemId);
		if (path != null) {
			try {
				InputSource is = new InputSource(IOUtils.getStreamForPath(path));
				is.setEncoding("UTF-8");
				return is;
			} catch (Exception e) {}
		}
		return null;
	}
    
    protected static String getAttributeValueWithDefault(Attributes attr, String key, String defValue) {
        String s = attr.getValue(key);
        if (s != null) {
            return s;
        }
        return defValue;
    }
	public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (m_call != null) {
			CallDescription cd = (CallDescription)m_call.get(qName);
			if (cd != null) {
				// TODO: deal with handlers with different priorities
				startElement(cd.id, attributes);
				if (cd.readcontent) {
					curval = "";
				}
			}
		}
	}
	
	
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		super.endElement(uri, localName, qName);
		if (m_call != null) {
			CallDescription cd = (CallDescription)m_call.get(qName);
			if (cd != null) {
				// TODO: deal with handlers with different priorities
				endElement(cd.id);
				if (cd.readcontent) {
					curval = null;
				}
			}
		}
	}
    
	protected void startElement(int id, Attributes attributes) {
	}
	protected void endElement(int id) {
	}
}

class CallDescription {
	int id;
	int callmode;
	int constraint;
	boolean readcontent;
	CallDescription other = null;
	
	public CallDescription(int id, int constraint, int callmode, boolean readcontent) {
		this.id = id;
		this.callmode = callmode;
		this.constraint = constraint;
		this.readcontent = readcontent;
	}
}
