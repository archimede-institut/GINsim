package fr.univmrs.tagc.GINsim.regulatoryGraph.SBML;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.*;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import JSci.io.MathMLExpression;
import JSci.io.MathMLParser;
import JSci.maths.MathDouble;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;// just to get rescanSign() method
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.widgets.StackDialog;

public final class SBMLXpathParser {
	/** Creates a new instance of SbmlXpathParser */
	private GsRegulatoryGraph graph;
	protected File _FilePath;
	private String s_nodeOrder = "";
	private GsRegulatoryVertex vertex = null;
	public GsRegulatoryEdge edge = null;
	private GsVertexAttributesReader vareader = null;
	private GsEdgeAttributesReader ereader = null;

	private int vslevel = 0;

	private Map m_edges = new HashMap();
	private Map map;
	Map m_checkMaxValue;
	Map m_thresholds;
	static Pattern pattern;

	private Hashtable values;
	private Vector v_function;

	public SBMLXpathParser() {
	}

	public SBMLXpathParser(String filename) {
		try {
			this._FilePath = new File(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.graph = new GsRegulatoryGraph();
		values = new Hashtable();
		map = new HashMap();
		initialize();

	}

	public void initialize() {
		parse();
	}

	/**
	* Parsing sbml file
	*/
	public void parse() {
		Document document = null;
		try {
			/** create a SAXBuilder instance */
			SAXBuilder sxb = new SAXBuilder();
			document = sxb.build(_FilePath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		try {
			
			/** initialization of the root element. **/			
			Element racine = document.getRootElement();
			/**
			 * we need to declare every namespace of a file to avoid errors 
			 * when we will try to retrieve elements belonging at this namespace 
			 **/
			Namespace namespace1 = Namespace.getNamespace("sbml",
					"http://www.sbml.org/sbml/level3/version1/core");
			
			/** to get the model ID. */
			XPath xpa1 = XPath.newInstance("//sbml:model/@id");
			
			/** add this namespace (namespace1) from xpath namespace list **/ 		 
			xpa1.addNamespace(namespace1);
			
			/** Retrieve the model ID (graph name)  **/			
			String modelName = xpa1.valueOf(racine);
			try {
				graph.setGraphName(modelName);
			} catch (GsException e) {
				GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
			}
			vareader = graph.getGraphManager().getVertexAttributesReader();
			ereader = graph.getGraphManager().getEdgeAttributesReader();

			Namespace namespace = Namespace.getNamespace("qual",
					"http://sbml.org/Community/Wiki/SBML_Level_3_Proposals/Qualitative_Models");
			
			/** Search the list of species.**/
			XPath xpa = XPath.newInstance("//qual:listOfQualitativeSpecies");
			xpa.addNamespace(namespace);

			/** Search the transitions list **/
			XPath xpa2 = XPath.newInstance("//qual:listOfTransitions");
			xpa2.addNamespace(namespace);
			
			/** retrieves all nodes corresponding to the path:/model/listOfQualitativeSpecies. **/
			List results = xpa.selectNodes(racine);

			/** to retrieve species data **/			
			for (int i = 0; i < results.size(); i++) {
				Element obElement = (Element) results.get(i);
				byte maxvalue = 0;
				List elList = obElement.getChildren();
				Iterator it = elList.iterator();
				while (it.hasNext()) {
					try {
						Element elcurrent = (Element) it.next();						
						/** we need construct a string character with different nodes from the graph **/
						s_nodeOrder += elcurrent.getAttributeValue("id") + " ";
						if (s_nodeOrder == null) {
							throw new JDOMException("missing nodeOrder");
						}
						/** get species ID and species name **/
						String id = elcurrent.getAttributeValue("id");
						String name = elcurrent.getAttributeValue("name");
						if (name == null || name.equals("")) {
							name = null;
						}
						byte maxval = (byte) Integer.parseInt(elcurrent
								.getAttributeValue("maxLevel"));						
						/** set a vertex with a species data **/
						vertex = graph.addNewVertex(id, name, maxval);
						vertex.getV_logicalParameters().setUpdateDup(false);

						String s_basal = elcurrent.getAttributeValue("basevalue");
						if(s_basal != null)
						{
							byte basevalue = (byte)Integer.parseInt(s_basal);
							if(basevalue !=0)
							{
								vertex.addLogicalParameter(new GsLogicalParameter(basevalue), true);
							}
						}
						
						String input = elcurrent.getAttributeValue("boundaryCondition");
						if (input != null) {
							vertex.setInput(input.equalsIgnoreCase("true") || input.equals("1"),
									graph);
						}
						/** add this vertex in a vertex collection **/
						values.put(vertex, new Hashtable());
					} catch (NumberFormatException e) {
						throw new JDOMException("mal formed node's parameter");// TODO:
																				// handle
																				// exception
					}
				}
			}
			/** to deal transition list in order to retrieve his all data **/
			List listOfTransition = xpa2.selectNodes(racine);
			for (int i = 0; i < listOfTransition.size(); i++) {
				Element transition = (Element) listOfTransition.get(i);
				List allTransitionElement = transition.getChildren();
				Element transElem = null;

				for (int k = 0; k < allTransitionElement.size(); k++) {
					/** retrieve a transition element */
					transElem = (Element) allTransitionElement.get(k);
					String trans_Id = transElem.getAttributeValue("id");
					/** retrieve children of transition element */
					List transChildren = transElem.getChildren(); 	
					
					/** retrieve output data **/
					Element listOfOutput = (Element) transChildren.get(1);
					List outputElemList = listOfOutput.getChildren();
					Element output = (Element) outputElemList.get(0);
					String qualSpecies = output.getAttributeValue("qualitativeSpecies");
					String transEffect = output.getAttributeValue("transitionEffect");
					Element listOfFunctionTerm = (Element) transChildren.get(2);
					List functTermChildren = listOfFunctionTerm.getChildren();
					/** retrieve <defaultTerm> element */
					Element defaultTerm = (Element) functTermChildren.get(0);
					String fctResultLevel = null;						
					for (int j = 1; j < functTermChildren.size(); j++) 
					{
						v_function = new Vector();
						
						/** to get all data in every <functionTerm> element **/
						Element functionTerm = (Element) functTermChildren.get(j);
						fctResultLevel = functionTerm.getAttributeValue("resultLevel");
						StringBuffer sb = deal(functionTerm); 																																					
						v_function.addElement(sb.toString());
						String myVertex = null;
						for (Enumeration enumvertex = values.keys(); enumvertex.hasMoreElements();) 
						{
							vertex = (GsRegulatoryVertex) enumvertex.nextElement();
							String vertexName = vertex.toString();
							if(qualSpecies.equals(vertexName))
							{								
								((Hashtable) values.get(vertex)).put(fctResultLevel, v_function);							
							}
						}						
					} 
					
					/** retrieve input data **/
					Element listOfInput = (Element) transChildren.get(0);
					List inputElemList = listOfInput.getChildren();
					/** retrieve children of <listOfInputs> element */
					for (int p = 0; p < inputElemList.size(); p++) {
						try {
							Element input = (Element) inputElemList.get(p);

							String qualitativeSpecies = ((Element) input)
									.getAttributeValue("qualitativeSpecies");
							/** to get the SBOTerm value from the current edge **/
							String sign = ((Element) input).getAttributeValue("sign");
							if (sign.equals("SBO:0000020"))
								sign = "negative";
							else if (sign.equals("SBO:0000459"))
								sign = "positive";
							else
								sign = "unknown"; 
							String transitionEffect = ((Element) input)
									.getAttributeValue("transitionEffect");
							String boundaryCondition = ((Element) input)
									.getAttributeValue("boundaryCondition");
							String to = getNodeId(trans_Id);								
					        byte minv = 1;
					        
					        if(m_thresholds.containsKey(qualitativeSpecies)){					        	
					        		minv = (byte) ((MathDouble) m_thresholds.get(qualitativeSpecies)).intValue();
					        }
							String maximumvalue = ((Element)input).getAttributeValue("maxvalue");							
							String smax = getAttributeValueWithDefault(maximumvalue, "-1");
							byte maxvalue = -2;
							/** add this edge in a graph **/
							edge = graph.addNewEdge(qualitativeSpecies, to, minv, sign);							
							if (smax.startsWith("m")) {
                            	maxvalue = -1; 
                            } else 
                            {
                            	maxvalue = (byte)Integer.parseInt(smax);
                            }
							storeMaxValueForCheck(edge, maxvalue);
							m_edges.put(qualitativeSpecies, edge);
							edge.me.rescanSign(graph);
							ereader.setEdge(edge.me);
						}
						catch (NumberFormatException e) {
							throw new JDOMException("mal formed interaction's parameters");
						}
					} 
				} 
			} 
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		placeInteractions();
		placeNodeOrder();
		graph.setSaveMode(vslevel);
		if (!values.isEmpty()) {
			parseBooleanFunctions();
		}
		Iterator it = graph.getNodeOrder().iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) it.next();
			vertex.getV_logicalParameters().cleanupDup();
		}
	} // void parse(File _FilePath)

	/** this function allows to obtain a string which
	 *  contain a logical function of every <functionTerm> element 
	 *  **/
	public StringBuffer deal(Element root) throws SAXException, IOException {
		List rootConv = root.getChildren();
		Element math = (Element) rootConv.get(0);
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		String xml = outputer.outputString(math);

		InputSource file = new InputSource(new ByteArrayInputStream(xml.getBytes()));
		
		/** we use MathMLExpression (JSci API) to easily parse 
		 ** and obtain data located in a <math> element.
		 ** each <math> element has been transformed into a file **/
		MathMLParser parser = new MathMLParser();
		parser.parse(file);

		int nLevel = 0;
		Object[] parseList = parser.translateToJSciObjects();
		StringBuffer sb = new StringBuffer();
		m_thresholds = new HashMap();
		for (int i = 0; i < parseList.length; i++) {
			Object o = parseList[i];
			MathMLExpression expr = (MathMLExpression) o;
			exploreExpression(expr, nLevel, sb, m_thresholds);
		}
		return sb;
	}

	/** This function writes the logical function of
	 ** every <functionTerm> as a string 
	 **/
	public void exploreExpression(Object expression, int level, StringBuffer sb, Map m_t) {
		if (expression instanceof MathMLExpression) {
			MathMLExpression mexpr = (MathMLExpression) expression;
			String op = mexpr.getOperation();
			if (op.equals("lt") || op.equals("geq") || op.equals("leq") || op.equals("gt")) {
				String chaine = null;
				if (op.equals("leq") || op.equals("lt")) {
					op = "!";
					chaine = op + mexpr.getArgument(0);
				} else if (op.equals("gt") || op.equals("geq")) {
					op = "";
					chaine = op + mexpr.getArgument(0);
					m_t.put(mexpr.getArgument(0), mexpr.getArgument(1));
				}
				sb.append(chaine);
			} else {
				if (mexpr.length() > 1)
					sb.append("(");
				for (int i = 0; i < mexpr.length(); i++) {
					exploreExpression(mexpr.getArgument(i), level + 1, sb, m_t);
					if (i < mexpr.length() - 1) {
						if (op.equals("and")) {
							sb.append("&");
						} else if (op.equals("or")) {
							sb.append("|");
						}
					}
				}
				if (mexpr.length() > 1)
					sb.append(")");
			}
		} else {
			sb.append("We have a serious problem !");
		}
	}

	/** To get vertex ID corresponding to the current <transition> element **/
	public String getNodeId(String transId) {
		String nodeName = null;
		pattern = Pattern.compile("tr_(.*)");
		Matcher matcher = pattern.matcher(transId);
		if(matcher.matches()){
			nodeName = matcher.group(1);
		}
		return nodeName;
	}

	private void storeMaxValueForCheck(GsRegulatoryEdge key, byte maxvalue) {
		if (m_checkMaxValue == null) {
			m_checkMaxValue = new HashMap();
		}
		m_checkMaxValue.put(key, new Integer(maxvalue));
	}

	/** To place different interactions between nodes **/
	private void placeInteractions() {
		// check the maxvalues of all interactions first
		if (m_checkMaxValue != null) {
			Map m = null;
			Iterator it = m_checkMaxValue.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				byte m1 = ((GsRegulatoryEdge) entry.getKey()).getMax();				
				byte m2 = ((Integer) entry.getValue()).byteValue();				
				byte max = ((GsRegulatoryEdge) entry.getKey()).me.getSource().getMaxValue();				
				if (m1 != m2) {
					if (m == null) { 
						m = new HashMap();
					}
					if (m1 == -1 && m2 == max || m2 == -1 && m1 == max) {
						m.put(entry, "");
					} else {
						m.put(entry, null);
					}
				}
			} 
			if (m != null) { 
				graph.addNotificationMessage(new GsGraphNotificationMessage(graph,
						"inconsistency in some interactions", new InteractionInconsistencyAction(),
						m, GsGraphNotificationMessage.NOTIFICATION_WARNING_LONG));
			}			
		}
	} // void placeInteractions()

	/** To place every node in the graph **/
	private void placeNodeOrder() {
		Vector v_order = new Vector();
		String[] t_order = s_nodeOrder.split(" ");
		for (int i = 0; i < t_order.length; i++) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) graph.getGraphManager()
					.getVertexByName(t_order[i]);
			if (vertex == null) {
				// ok = false;
				break;
			}
			v_order.add(vertex);
		}
		if (v_order.size() != graph.getGraphManager().getVertexCount()) {
			// error
			Tools.error("incoherent nodeOrder, not restoring it", null);
		} else {
			graph.setNodeOrder(v_order);
		}
	}

	/** To parse each logical function **/
	private void parseBooleanFunctions() {
		List allowedEdges;
		GsRegulatoryVertex vertex;
		String value, exp;
		try {
			for (Enumeration enu_vertex = values.keys(); enu_vertex.hasMoreElements();) {
				vertex = (GsRegulatoryVertex) enu_vertex.nextElement();
				allowedEdges = graph.getGraphManager().getIncomingEdges(vertex);
				if (allowedEdges.size() > 0) {
					for (Enumeration enu_values = ((Hashtable) values.get(vertex)).keys(); enu_values
							.hasMoreElements();) {
						value = (String) enu_values.nextElement();
						for (Enumeration enu_exp = ((Vector) ((Hashtable) values.get(vertex))
								.get(value)).elements(); enu_exp.hasMoreElements();) {
							exp = (String) enu_exp.nextElement();
							addExpression(Byte.parseByte(value), vertex, exp);
						}
					}
					vertex.getInteractionsModel().parseFunctions(); 
					if (vertex.getMaxValue() + 1 == ((Hashtable) values.get(vertex)).size()) {
						((GsTreeElement) vertex.getInteractionsModel().getRoot()).setProperty(
								"add", new Boolean(false));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addExpression(byte val, GsRegulatoryVertex vertex, String exp) {
		try {
			GsBooleanParser tbp = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(
					vertex));
			GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
			if (!tbp.compile(exp, graph, vertex)) {
				InvalidFunctionNotificationAction a = new InvalidFunctionNotificationAction();
				Vector o = new Vector();
				o.addElement(new Short(val));
				o.addElement(vertex);
				o.addElement(exp);
				graph.addNotificationMessage(new GsGraphNotificationMessage(graph,
						"Invalid formula : " + exp, a, o,
						GsGraphNotificationMessage.NOTIFICATION_WARNING));				
			} else {
				interactionList.addExpression(val, vertex, tbp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public GsGraph getGraph() {
		return graph;
	}

    public String getAttributeValueWithDefault(String atValue, String defValue)
    {  	
	    if (atValue != null) 
	    {	
	    	return atValue;
	    }
	    return defValue;
	}
	
	/**  **/
	class InteractionInconsistencyAction implements GsGraphNotificationAction {
		public String[] getActionName() {
			String t[] = { "view" };
			return t;
		}

		public boolean perform(GsGraph graph, Object data, int index) {
			StackDialog d = new InteractionInconsistencyDialog((Map) data, graph,
					"interactionInconststancy", 200, 150);
			d.setVisible(true);
			return true;
		}

		public boolean timeout(GsGraph graph, Object data) {
			return true;
		}
	} // class InteractionInconsistencyAction

	class InteractionInconsistencyDialog extends StackDialog {
		private static final long serialVersionUID = 4607140440879983498L;

		GsRegulatoryGraph graph;
		Map m;
		JPanel panel = null;

		public InteractionInconsistencyDialog(Map m, GsGraph graph, String msg, int w, int h) {
			super(graph.getGraphManager().getMainFrame(), msg, w, h);
			this.graph = (GsRegulatoryGraph) graph;
			this.m = m;
			setMainPanel(getMainPanel());
		}

		private JPanel getMainPanel() {
			if (panel == null) {
				panel = new JPanel();
				JTextArea txt = new JTextArea();
				String s1 = "";
				String s2 = "";
				Iterator it = m.entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					Entry e2 = (Entry) entry.getKey();
					GsRegulatoryEdge edge = (GsRegulatoryEdge) e2.getKey();
					byte oldmax = ((Integer) e2.getValue()).byteValue();
					if (entry.getValue() == null) {
						s1 += edge.getLongDetail(" ") + ": max should be "
								+ (oldmax == -1 ? "max" : "" + oldmax) + "\n";
					} else {
						s2 += edge.getLongDetail(" ") + ": max was explicitely set to " + oldmax
								+ "\n";
					}
				}
				if (s1 != "") {
					s1 = "potential problems:\n" + s1 + "\n\n";
				}
				if (s2 != "") {
					s1 = s1 + "warnings only:\n" + s2;
				}
				txt.setText(s1);
				txt.setEditable(false);
				panel.add(txt);
			}
			return panel;
		}

		public void run() {
			// TODO: propose some automatic corrections
		}
	} // class InteractionInconsistencyDialog
	
	class InvalidFunctionNotificationAction implements GsGraphNotificationAction {

		public InvalidFunctionNotificationAction() {
			super();
		}

		public boolean timeout(GsGraph graph, Object data) {
			return false;
		}

		public boolean perform(GsGraph graph, Object data, int index) {
			Vector v = (Vector) data;
			byte value = ((Short) v.elementAt(0)).byteValue();
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) v.elementAt(1);
			String exp = (String) v.elementAt(2);
			boolean ok = true;
			switch (index) {
			case 0:
				try {
					GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
					GsTreeExpression texp = interactionList.addEmptyExpression(value, vertex);
					texp.setText(exp);
					texp.setProperty("invalid", new Boolean("true"));
				} catch (Exception ex) {
					ex.printStackTrace();
					ok = false;
				}
				break;
			case 1:
				break;
			}
			return ok;
		}

		public String[] getActionName() {
			String[] t = { "Keep function", "Discard function" };
			return t;
		}
	} // class InvalidFunctionNotificationAction
}
