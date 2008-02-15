package fr.univmrs.tagc.common.xml;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 */
abstract public class XMLHelper extends DefaultHandler implements EntityResolver {

	protected static final int NOCALL = 0;
	protected static final int ENDONLY = 1;
	protected static final int STARTONLY = 2;
	protected static final int BOTH = 3;
	
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
    
	private static Map m_dtdError = null;
	
	protected Map m_call = null;
	
	public void error(SAXParseException e) throws SAXException {
	    if (!showError) {
	        return;
	    }
        Object[] t = {Translator.getString("STR_stop"), Translator.getString("STR_goOn"), Translator.getString("STR_ignoreNext")};
	    int ret = JOptionPane.showOptionDialog(null, 
                Translator.getString("STR_errorOccured")+"\n\n"+
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
		Tools.error(new GsException(GsException.GRAVITY_NORMAL, e.getLocalizedMessage()
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
        	Tools.error(new GsException(GsException.GRAVITY_ERROR, e), null);
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

        //s_filename = file.getAbsolutePath();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(validating);
		try {
			SAXParser sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.setEntityResolver(this);
			xr.setErrorHandler(this);
			//FileReader r = new FileReader(file);
			xr.parse(new InputSource(new InputStreamReader(is)));
		} catch (FileNotFoundException e) { 
		    Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (IOException e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (ParserConfigurationException e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		} catch (SAXParseException e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        } catch (SAXException e) {
        	Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    /**
     * get the local DTD file (full path, including the protocol (file://))
     * @return an url path to the fallbackDTD
     */
    public String getFallBackDTD() {
    	return null;
    }
    
    /**
     * when the entityresolver has choosen it's DTD, it will notify the parser using this function.
     * overwrite me if you want it to do something...
     * you can also read the value in the <code>s_dtd</code> field.
     * 
     * @param s_dtd
     */
    protected void setUsedDTD(String s_dtd) {
        
        // check if the DTD name/version is the same as the local one
        String s = getFallBackDTD();
        String lfilename = s.substring(s.lastIndexOf(File.separator)+1);
        String sf = s_dtd.substring(s_dtd.lastIndexOf(File.separator)+1);
        
        // if filenames differs: use the local one instead (to avoid saving the file with a bad DTD field)
        if (!lfilename.equals(sf)) {
            this.s_dtd = s;
        } else {
            this.s_dtd = s_dtd;
        }
    }
    
    /**
     * do the "entityresolver" job, a bit lazy, this needs some cleanups to become 
     * more generalist, but it does the job for ginsim...
     * 
     * @param publicId
     * @param systemId
     * @return an input source for the DTD; or null to use the default
     * @throws SAXException
     */
	public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException {
		try {
			if (testURL(systemId)) {
	            setUsedDTD(systemId);
			    return null;
	        }
	        
			String s = getFallBackDTD();
            if (s == null || s.length() <= 7) {
                return null;
            }
            InputStream stream = ClassLoader.getSystemResourceAsStream(s.substring(7));
            	if ( stream != null) {
           			if (!s.equals(systemId) && !dtdErorExists(systemId)) {
           				Tools.error(new GsException(GsException.GRAVITY_INFO, systemId+":\n "+Translator.getString("STR_revert2defaultDTD")), null);
            			}
            			setUsedDTD(s);
            			return new InputSource(stream);
            	}
            	Tools.error(new GsException(GsException.GRAVITY_INFO, systemId+":\n "+Translator.getString("STR_revert2defaultDTD")+"\n\nFAILED to use the local DTD, please check your ginsim installation"), null);
		} catch (IOException e) {}
		return null;
	}
    
    /**
     * test if a DTD path is ok.
     * @param s_url
     * @return true if this DTD can be reached 
     * @throws IOException
     */    
    private boolean testURL(String s_url) throws IOException {
        File dtdFile = null;
        URL url = new URL(s_url); // dtd path in ginml
        
        // "file://" in DOCTYPE element
        if (url.getProtocol().compareTo("file") == 0) {
            dtdFile = new File(url.getPath());
            // If DTD file does not exist, return local dtd
            if (dtdFile.exists()) {
                try {
                    new FileInputStream(dtdFile).close();
                    return true;
                } catch (FileNotFoundException e) {
                    return false;
                }
            }
        } else if (url.getProtocol().compareTo("http") == 0) {
            try { // check if the specified host/port can be reached
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();    
                connection.connect();
                if (connection.getResponseCode() == 200) { // OK
                    setUsedDTD(s_url);
                    return true;
                }
            } catch (Exception e){ // if no internet connection
                return false;
            }
        }
        return false;
    }

    /**
     * @param systemId
     * @return true if an error has already been shown for this DTD
     */
    private static boolean dtdErorExists(String systemId) {
        if (m_dtdError == null) {
            m_dtdError = new HashMap();
            m_dtdError.put(systemId, null);
            return false;
        }
        if (!m_dtdError.containsKey(systemId)) {
            m_dtdError.put(systemId, null);
            return false;
        }
        return true;
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