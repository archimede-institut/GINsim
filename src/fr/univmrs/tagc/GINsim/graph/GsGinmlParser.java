package fr.univmrs.tagc.GINsim.graph;

import java.io.InputStream;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.connectivity.GsReducedGraphParser;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicParser;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraphParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryParser;
import fr.univmrs.tagc.GINsim.xml.GsXMLHelper;
import fr.univmrs.tagc.common.xml.XMLHelper;

/**
 * parses a ginml regulatory graph.
 */
public final class GsGinmlParser extends XMLHelper {
    
    private Map map;
    private GsXMLHelper realParser = null;
    
    static {
        String DTD = "/fr/univmrs/tagc/GINsim/resources/GINML_2_1.dtd";
        XMLHelper.addEntity("http://gin.univ-mrs.fr/GINsim/GINML_2_0.dtd", DTD);
        XMLHelper.addEntity("http://gin.univ-mrs.fr/GINsim/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/ibdm/GINsim/resources/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/ibdm/GINsim/resources/GINML_2_0.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/tagc/GINsim/resources/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/tagc/GINsim/resources/GINML_2_0.dtd", DTD);
    }
    
    /**
     * @param is from where to read
     * @param map
     * @return the new graph
     */
    public GsGraph  parse(InputStream is, Map map) {
    	this.map = map;
		startParsing(is);
		if (realParser == null) {
		    return null;
		}
		return realParser.getGraph();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equals("graph")) {
            String s_class = attributes.getValue("class");
            if ("regulatory".equals(s_class)) {
                realParser = new GsRegulatoryParser(map, attributes, s_dtd, s_filename);
            } else if ("dynamical".equals(s_class)) {
                realParser = new GsDynamicParser(map, attributes, s_dtd, s_filename);
            } else if ("reduced".equals(s_class)) {
                realParser = new GsReducedGraphParser(map, attributes, s_dtd, s_filename);
            } else if ("hierarchicalTransitionGraph".equals(s_class)) {
                realParser = new GsHierarchicalTransitionGraphParser(map, attributes, s_dtd, s_filename);
            } else {
                throw new SAXException("bad type of graph");
            }
            xr.setContentHandler(realParser);
        }
    }
    
    public GsGraph getGraph() {
		if (realParser == null) {
		    return null;
		}
		return realParser.getGraph();
    }
}
