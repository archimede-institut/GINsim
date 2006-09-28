package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;

/**
 * restore GsSimulationParameters from a saved file
 */
public class GsReg2dynParametersParser extends GsXMLHelper{

    XMLReader xr;
    ContentHandler handler;
    
    /**
     * create a transient handler to restore simulation parameters.
     * 
     * @param xr
     * @param handler
     */
    public GsReg2dynParametersParser(XMLReader xr, ContentHandler handler) {
        super();
        this.xr = xr;
        this.handler = handler;
    }
    
    public GsGraph getGraph() {
        return null;
    }
    public String getFallBackDTD() {
        return null;
    }

    private static final int MODE = 1;
    private static final int STATES = 2;
    private static final int BLOCKERS = 3;
    
    Map m = new HashMap();
    GsSimulationParameters params;
    int reading = 0;
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("node".equals(qName)) {
            params = new GsSimulationParameters();
            params.name = attributes.getValue("name");
        } else if ("attr".equals(qName)) {
            String s_name = attributes.getValue("name");
            if ("mode".equals(s_name)) {
                
            } else if ("initialStates".equals(s_name)) {
                
            }
        } else if ("string".equals(qName)) {
            curval = "";
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("graph".equals(qName)) {
            // go back to the previous handler
            xr.setContentHandler(handler);
        } else if ("node".equals(qName)) {
            m.put(params.name, params);
        } else if ("string".equals(qName)) {
            curval = curval.trim();
            switch (reading) {
                case MODE:
                    if ("synchronous".equals(curval)) {
                        params.mode = Simulation.SEARCH_SYNCHRONE;
                    } else if ("asynchronous_bf".equals(curval)) {
                        params.mode = Simulation.SEARCH_ASYNCHRONE_BF;
                    } else if ("asynchronous_df".equals(curval)) {
                        params.mode = Simulation.SEARCH_ASYNCHRONE_DF;
                    } else if ("priorityClasses".equals(curval)) {
                        params.mode = Simulation.SEARCH_BYPRIORITYCLASS;
                    }
                    break;
                case STATES:
                    break;
                case BLOCKERS:
                    break;
                default:
                    break;
            }
            curval = null;
        }
    }
}
