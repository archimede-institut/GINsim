package fr.univmrs.tagc.GINsim.regulatoryGraph.SBML;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import JSci.io.MathMLParser;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
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

	static Pattern pattern;

	private Hashtable values;
	private Vector v_function;

	public SBMLXpathParser() {
	}

	public SBMLXpathParser(String filename) {

		this._FilePath = new File(filename);
		this.graph = new GsRegulatoryGraph();
		values = new Hashtable();
		initialize();
	}

	public void initialize()  {
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
				graph.setSaveFileName(modelName);
			} catch (Exception e) {
				e.printStackTrace();
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
			Map m_maxvalues = null;
			Vector v_nodeOrder = null;
			for (int i = 0; i < results.size(); i++) {
				Element obElement = (Element) results.get(i);
				List elList = obElement.getChildren();
				Iterator it = elList.iterator();
				v_nodeOrder = new Vector<String>();
				while (it.hasNext()) {
					try {
						Element elcurrent = (Element) it.next();						
						/** we need construct a string character with different nodes from the graph **/
						s_nodeOrder += elcurrent.getAttributeValue("id") + " ";
						v_nodeOrder.add(elcurrent.getAttributeValue("id"));
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
			graph.setNodeOrder(v_nodeOrder);
			
			/** to deal transition list in order to retrieve his all data **/
			List listOfTransition = xpa2.selectNodes(racine);
			for (int i = 0; i < listOfTransition.size(); i++) {
				Element transition = (Element) listOfTransition.get(i);
				List allTransitionElement = transition.getChildren();
				Element transElem = null;

				for (int k = 0; k < allTransitionElement.size(); k++) {
					/** retrieve a transition element */
					transElem = (Element) allTransitionElement.get(k);
					String trans_Id = transElem.getAttributeValue("id"); //transition id
					
					/** retrieve children of transition element */
					List transChildren = transElem.getChildren(); 	
					
					/** Retrieve the list of inputs **/
					Element listOfInput = (Element) transChildren.get(0);
					List inputElemList = listOfInput.getChildren();
					HashMap<String, String> intput_to_sign = new HashMap<String, String>();
					for (int p = 0; p < inputElemList.size(); p++) { 
						Element input = (Element) inputElemList.get(p);
						String node_from = ((Element) input)
								.getAttributeValue("qualitativeSpecies");
						/** to get the SBOTerm value from the current edge **/
						String sign = ((Element) input).getAttributeValue("sign");
						intput_to_sign.put( node_from, sign);
					}
					
					/** retrieve output data **/
					Element listOfOutput = (Element) transChildren.get(1);
					List outputElemList = listOfOutput.getChildren();
					Element output = (Element) outputElemList.get(0); // output element
					String qualSpecies = output.getAttributeValue("qualitativeSpecies");
					
					/** retrieve the lits of function terms **/
					Element listOfFunctionTerm = (Element) transChildren.get(2);
					List functTermChildren = listOfFunctionTerm.getChildren();				
					/** retrieve <defaultTerm> element */
					Element defaultTerm = (Element) functTermChildren.get(0);
					String dft_resulLevel = defaultTerm.getAttributeValue("resultLevel");
					byte dft_value = (byte)Integer.parseInt(dft_resulLevel);		
					if(!(dft_resulLevel.equals("0"))){
						for (Enumeration enumvertex = values.keys(); enumvertex.hasMoreElements();) 
						{
							vertex = (GsRegulatoryVertex) enumvertex.nextElement();
							String vertexName = vertex.toString();
							if(qualSpecies.equals(vertexName))
							{								
								vertex.addLogicalParameter(new GsLogicalParameter(dft_value), true); 
							}
						}
					}
					
					String fctResultLevel = null;					
					for (int j = 1; j < functTermChildren.size(); j++) 
					{
						v_function = new Vector();	
						
						/** to get all data in every <functionTerm> element **/
						Element function_term_element = (Element) functTermChildren.get(j);
						fctResultLevel = function_term_element.getAttributeValue("resultLevel");
						FunctionTerm function_term = deal( function_term_element);
						if( function_term != null) {
							createMutliEdges( function_term, getNodeId(trans_Id), intput_to_sign, graph);
							v_function.addElement( function_term.getLogicalFunction());
						}
			
						for (Enumeration enumvertex = values.keys(); enumvertex.hasMoreElements();) 
						{
							vertex = (GsRegulatoryVertex) enumvertex.nextElement();
							String vertexName = vertex.getId();
							if(qualSpecies.equals(vertexName))
							{								
								((Hashtable) values.get(vertex)).put(fctResultLevel, v_function);							
							}
						}						
					} // </functionTerm>
				} // </transition> 
			}  
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

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
	
	/** This function allows to obtain a string which
	 *  contain a logical function of every <functionTerm> element 
	 *  We use MathMLExpression (JSci API) to easily parse 
	 *  and obtain a String that represent all contain (data) located in a <math> element.
	 *  For instance, if we have this :   <math>
	 *										<apply>
	 *										  <or/>
	 *											<apply>
	 *											  <and/>
	 *												<apply>
	 *											      <lt/>
	 *											      <ci>GO</ci>
	 *											      <ci>1</ci>
	 *											    </apply>
	 *											    <apply>
	 *											      <geq/>
	 *											      <ci>G2</ci>
	 *											      <ci>2</ci>
	 *											    </apply>
	 *											</apply>
	 *										    <apply>
	 *											.......
	 *										    </apply>
	 *										 </apply>
	 *									   </math>		 
	 * The JSci API allows to obtain all contain representing this <math> element like this :
	 *				" G0.lt(new MathDouble(1)).and(G2.geq(new MathDouble(2))).or(.....) "
	 * Thus, we make some tranformations on this String to get a representation which correspond 
	 * a logical function of GINsim, like this : " (!G0:1&G2:2) | (...) " 
	 **/
	
	public FunctionTerm deal(Element root) throws SAXException, IOException {
		List rootConv = root.getChildren();
		Element math = (Element) rootConv.get(0);
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		String xml = outputer.outputString(math);
		InputSource file = new InputSource(new ByteArrayInputStream(xml.getBytes()));
		
		MathMLParser parser = new MathMLParser();
		parser.parse(file);
		FunctionTerm function = new FunctionTerm("or");
		Object[] parseList = parser.translateToJSciCode();
		
		 for (Object o : parseList)
		 {
			String exp_Object = (String)o;
			HashMap< String, ArrayList<String>> cond_to_exp = MathMLCodeParser.getSBMLLogicalFunction(exp_Object);
			if(cond_to_exp == null) {
				return null;
			}
			Iterator<String> it_cond = cond_to_exp.keySet().iterator();
			while (it_cond.hasNext()) {
				String cond =  it_cond.next();
				Condition condition = new Condition("and");
				function.addCondition( condition);
				ArrayList<String> expressions = cond_to_exp.get(cond);
				
				for(int i = 0; i< expressions.size(); i++){
					Expression expression = new Expression(expressions.get(i), graph);
					if( !expression.getNode().isEmpty()){
						condition.addExpression( expression);						
					}	
				}
			}			
		 }
		 
		return function;
	}
	
	
	private void createMutliEdges( FunctionTerm function_term, String node_to_id, HashMap<String, String> input_to_sign, GsRegulatoryGraph graph){		
				
		List<Condition> l_condition = function_term.getConditionList();
		Iterator<Condition> it_cond = l_condition.iterator();
		while( it_cond.hasNext()){
			Condition condition = it_cond.next(); 
			List<Expression> l_exp = condition.getExpressionList();
			Iterator<Expression> it_exp = l_exp.iterator();
			while( it_exp.hasNext()){
				Expression expression = it_exp.next();
				String node_from_id = expression.getNode();
				String sign_code = input_to_sign.get( node_from_id);
				byte sign;
				if ( "SBO:0000020".equals(sign_code)){
					sign = GsRegulatoryMultiEdge.SIGN_NEGATIVE;
				}
				else if ( "SBO:0000459".equals(sign_code)){
					sign = GsRegulatoryMultiEdge.SIGN_POSITIVE;
				}
				else{
					sign = GsRegulatoryMultiEdge.SIGN_UNKNOWN;
				}	
				Vector<String> list_cases = expression.getAllCases();
				
				for( int i = 0; i < list_cases.size(); i++) {
					String exp = list_cases.get(i);
					int index = exp.indexOf(":");
					if( index >= 0 && index < exp.length()-1) {
						byte threshold = (byte) Integer.parseInt( exp.substring( index+1));
						edge = graph.addNewEdge( node_from_id, node_to_id, threshold, sign);
						m_edges.put(sign_code, edge);
						edge.me.rescanSign(graph);
						ereader.setEdge(edge.me);
					}
				}
			}
		}
	}
	
	
	/** This class parse a JSci String that correspond to all data located in <math> element from every
	 *  <functionTerm> element of a <transition> tag. **/
	
	static class MathMLCodeParser {
		
		public static HashMap< String, ArrayList<String>> getSBMLLogicalFunction(String code) {
			
			code = MathMLCodeParser.generateLogicalFunction( code);
			if(code.isEmpty()) {
				return null;
			}

			ArrayList<String> conditions = MathMLCodeParser.getConditions( code);
			
			HashMap< String, ArrayList<String>> final_result = new HashMap<String, ArrayList<String>>();
			
			for( int i = 0; i < conditions.size(); i++){
				String condition = conditions.get( i);
				ArrayList<String> expressions = MathMLCodeParser.getExpressions( condition);
				final_result.put(condition, expressions);
			}
			return final_result;
		}
		
		/** **/
		private static ArrayList<String> getExpressions( String code){
			
			ArrayList<String> expressions = new ArrayList<String>();
			
			int old_index = 0;	
			int new_index;
			do{
				new_index = code.indexOf( "&", old_index);
				if( new_index >=0){
					expressions.add( code.substring( old_index, new_index));
					old_index = new_index +1;
				}
				else{
					expressions.add( code.substring( old_index));
				}
			}while( new_index >= 0);
			
			return expressions;
		}
		
		private static ArrayList<String> getConditions( String code){
			
			ArrayList<String> conditions = new ArrayList<String>();
			
			int old_index = 0;	
			int new_index;
			do{
				new_index = code.indexOf( "|", old_index);
				if( new_index >=0){
					conditions.add( code.substring( old_index, new_index));
					old_index = new_index +1;
				}
				else{
					conditions.add( code.substring( old_index));
				}
			}while( new_index >= 0);
			
			return conditions;
		}
		
		
		private static String generateLogicalFunction( String code){
			
			code = MathMLCodeParser.replaceMathDouble( code);
			
			code = MathMLCodeParser.replaceGEQ( code);
			
			code = MathMLCodeParser.replaceLT( code);

			code = MathMLCodeParser.replaceAnd( code);
			
			code = MathMLCodeParser.replaceOr( code);
			
			return code ;
		}
		
		/**
		 * Replace some caracters identify by "new MathDouble(int)" in a JSci String by an Integer.
		 * i.e : if we have a JSci String like this :" G0.lt(new MathDouble(1)) ", thus we can
		 * replace "new MathDouble(1)" by "1". This String become: " G0.lt(:1) ".
		 * @return String.
		 */		
		private static String replaceMathDouble( String code){
			
			Pattern p = Pattern.compile( "new MathDouble\\((\\d)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, m.group(1));
			}
			m.appendTail(sb);
			
			return sb.toString();
		}
		
		
		/**
		 * Replace a "geq" String by ">=".
		 * @return String without geq. 
		 * For instance : " G0.geq(:1) " become : " G0 >= 1) "
		 */	
		private static String replaceGEQ( String code){
			
			Pattern p = Pattern.compile( "([^\\(\\)\\.]*)\\.geq\\((\\d)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb,  m.group(1) + ">=" + m.group(2));
			}
			m.appendTail(sb);
			
			return sb.toString();
		}
		
		
		/**
		 * Replace a "lt" String by "<".
		 * @return String without geq. 
		 * For instance : " G0.lt(:1) " become : " G0 < 1) "
		 */	
		private static String replaceLT( String code){
			
			Pattern p = Pattern.compile( "([^\\(\\)\\.]*)\\.lt\\((\\d)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String digit = m.group(2);
				if( "1".equals( digit)){
					m.appendReplacement(sb, m.group(1) + "<" + digit);
				}
				else{
					m.appendReplacement(sb,  m.group(1) + "<" + m.group(2));
				}
			}
			m.appendTail(sb);
			
			return sb.toString();
		}
		
		/**
		 * Replace an "and" operator by "&".
		 * @return String without "and" operator. 
		 * For instance, this String: " G0.lt(new MathDouble(1)).and(G2.geq(new MathDouble(2))) " 
		 * become : " G0.lt(new MathDouble(1)) & (G2.geq(new MathDouble(2))) "
		 */	
		private static String replaceAnd( String code){
			
			Pattern p = Pattern.compile( "\\.and\\(([^\\(\\)\\.]*)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "&" + m.group(1));
			}
			m.appendTail(sb);
			
			return sb.toString();
		}
		
		
		/**
		 * Replace an "or" operator by "|".
		 * @return String without "or" operator. 
		 * For instance, this String: " G0.lt(new MathDouble(1)).or(G2.geq(new MathDouble(2))) " 
		 * become : " G0.lt(new MathDouble(1)) | (G2.geq(new MathDouble(2))) "
		 */	
		private static String replaceOr( String code){
			
			Pattern p = Pattern.compile( "\\.or\\(([^\\(\\)\\.]*)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "|" + m.group(1));
			}
			m.appendTail(sb);
			
			String result = sb.toString();
			if( "|".equals(result.substring( result.length() - 1))) {
				result = result.substring(0, result.length() - 1);
			}
			
			return result;
		}
		
	} // class MathMLCodeParser
	
	
	 public boolean characterInString(String chaine, char c)
	     {
	           boolean verite = false;
	           for(int i = 0; i < chaine.length(); i++)
	           {
	               if(chaine.charAt(i) == c)
	               {
	                     verite = true;
	                     break;
	               }
	           }
	         return verite;
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

	
	/** <p> An object Expression represent the contain of a "single" an apply element.
	 * i.e : <apply>
	 *         <geq/>
	 *           <ci>node id</ci> 
	 *           <cn>threshold value</cn> 
	 *       </apply>
	 *  </p>     
	 ** @author Guy-Ross ASSOUMOU
	 **/	
 class Expression {
	 	/** The node id*/	 
		private String node;
		
		/** The operator in this apply element, i.e : <geq/>. */
		private String operator;
		
		/** The threshold value in this apply element. */
		private int minvalue;
		/** The maxvalue of this input element (the regulatory vertex which is identify by the "node id"). */
		private int maxvalue;

		
		/* **************** CONSTRUCTORS ************/	
		
		public Expression() {
			this.node ="";
			this.operator = "";
			this.minvalue = 0;
			this.maxvalue = 0;
		}		

		/** To create an Expression Object with the maxvalue of every regulatory vertex of the graph. */
		public Expression(String str, GsRegulatoryGraph graph) {
			
			int index = str.indexOf("<");
			if( index >= 0) {
				node = str.substring( 0, index);
				operator = "lt";
				minvalue = Integer.parseInt( str.substring( index+1));
			}
			else{
				index = str.indexOf(">=");
				if( index >= 0) {
					node = str.substring( 0, index);
					operator = "geq";
					minvalue = Integer.parseInt( str.substring( index+2));
				}
				else {
					System.out.println("SBMLXpathParser.Expression.Expression() : unknown operator in expression: " + str);
					this.node ="";
					this.operator = "";
					this.minvalue = 0;
					this.maxvalue = 0;
				}
			}
			
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) graph.getGraphManager().getVertexByName(node);
			if( vertex != null) {
				maxvalue = vertex.getMaxValue();
			}			
		}
		
		public Expression(String node, String operator, int minvalue,
				int maxvalue) {

			this.node = node;
			this.operator = operator;
			this.minvalue = minvalue;
			this.maxvalue = maxvalue;
		}
		
		
		/* **************** GETTERS AND SETTERS ************/
		
		public String getNode() {
			return node;
		}
		public void setNode(String node) {
			this.node = node;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
		public int getMinvalue() {
			return minvalue;
		}
		public void setMinvalue(int minvalue) {
			this.minvalue = minvalue;
		}
		public int getMaxvalue() {
			return maxvalue;
		}
		public void setMaxvalue(int maxvalue) {
			this.maxvalue = maxvalue;
		}	

		
		/**
		 * To manage each case of representation of any <apply> element.
		 * @return a vector that contains a String which represents a combination 
		 * of node and his threshold value. For instance : "GO:1" where "GO" is a 
		 * node name and "1" is a threshold value for this node.
		 */
		 
		public Vector<String> getAllCases() {
			String op = getOperator();
			Vector<String> cases = new Vector<String>();
			
			if(op.equals("geq")){
				String cas = node + ":" + minvalue;
				cases.add( cas);
			}
			else if(op.equals("gt") && maxvalue > minvalue){
				for( int i = minvalue + 1; i <= maxvalue; i++){
					String cas = node + ":" + i;
					cases.add( cas);
				}
			}
			else if(op.equals("leq")){
				for( int i = minvalue; i >= 1; i--){
					String cas = "!" + node + ":" + i;
					cases.add( cas);
				}
			}
			else if(op.equals("lt") && minvalue > 0){
				String cas = "!"+ node + ":" + minvalue;
				cases.add( cas);
			}
			return cases;
		} 

		
		
		
		@Override
		public String toString() {
			return node + " " + operator +" " + minvalue + "/" + maxvalue + "\n";
		}
	}// class Expression
	
 
 /** An object Condition represent every first <apply> tag under the <or/> tag in SBML file.
  *  A condition contains mainly an operator ("and" operator always) and a list of Expression.
  * */
 class Condition {
	 
		private List<Expression> expressionList;
		private String op;
		private String op_symbol;
		
		/* **************** CONSTRUCTORS ************/	
		
		public Condition( String op) {
			expressionList = new ArrayList<SBMLXpathParser.Expression>();
			this.op = op;
			if( op.equals("and")){
				op_symbol = "&";
			}
			else if ( op.equals("or")){
				op_symbol = "|";
			}
			else{
				op_symbol="UNKNOWN";
			}
			
			
		}
		
		/* ****************  GETTERS  ************/	
		
		/** To get a list of Expressions that belong to this Condition */
		public List<Expression> getExpressionList() {
			return expressionList;
		}


		public Vector<String> getAllCases() {

			/** Generate an arraylist of vectors. Each vector contains all 
			 * the situations described by an expression
			 * For instance G0>=1 with G0={0,1,2} means G0:1 and G0:2 */
			ArrayList<Vector<String>> expressions = new ArrayList<Vector<String>>();
			Iterator<Expression> it = expressionList.iterator();
			while(it.hasNext()){
				expressions.add(it.next().getAllCases());
			}
			
			Vector<String> all_cases = new Vector<String>();
			all_cases.add("");
			
			/** Parse the liste of expressions vectors */
			Iterator<Vector<String>> exp_it = expressions.iterator();			
			while( exp_it.hasNext()){
				Vector<String> exp = exp_it.next();
				Iterator<String> case_it = exp.iterator();
				Vector<String> temp_all_cases = new Vector<String>();
				
				/** Parse the list of situations of a single expression */
				while( case_it.hasNext()){
					String current_exp_cas = case_it.next();
					Iterator<String> all_case_ite = all_cases.iterator();
					
					/** Duplicate the string perviously construct with the precedent expressions in order
					 * to generate all the entries related to the current situation lists */
					while( all_case_ite.hasNext()){
						String current_all_case = all_case_ite.next();
						String new_entry = current_all_case + op_symbol + current_exp_cas;
						if(new_entry.charAt(0) == '&') {
							new_entry = new_entry.substring(1);
						}
						
						temp_all_cases.add( new_entry);
					}
				}
				all_cases.clear();
				all_cases.addAll( temp_all_cases);
			}			
		
			return all_cases;
		}
		
		/** Add an Expression into a list */
		public void addExpression(Expression exp) {
			expressionList.add(exp);			
		}
		
		@Override
		public String toString() {
			String result = op + "\n";
			for (int i = 0; i < expressionList.size(); i++) {
				result += "|--|-- " + expressionList.get( i);
			}			
			return result;
		}
		
 	}// class Condition
	
 
 /** A FunctionTerm object represent a collection of Condition object.
  *  It contains also an operator (mainly "or" operator) **/
 
class FunctionTerm {		
		private List<Condition> conditionList;
		private String op;
		private String op_symbol;
		
		/* **************** CONSTRUCTORS ************/
		
		public FunctionTerm(String op) {
			conditionList = new ArrayList<SBMLXpathParser.Condition>();
			this.op = op;
			if( op.equals("and")){
				op_symbol = "&";
			}
			else if ( op.equals("or")){
				op_symbol = "|";
			}
			else{
				op_symbol="UNKNOWN";
			}
		}
		
		/* **************** GETTERS AND SETTERS ************/
		
		public String getOp() {
			return op;
		}


		public void setOp(String op) {
			this.op = op;
		}


		public List<Condition> getConditionList() {
			return conditionList;
		}	
		
		public void addCondition(Condition cond) {
			conditionList.add(cond);
			
		}
	
		/** @return a logical fiunction that correspond to  
		 * **/
		public String getLogicalFunction(){
			
			String logical_function = "";
			Iterator<Condition> cond_ite = conditionList.iterator();
			while( cond_ite.hasNext()){
				Condition cond = cond_ite.next();
				Vector<String> cond_cases = cond.getAllCases();
				String new_entry = "";
				Iterator<String> case_ite = cond_cases.iterator();
				
				while( case_ite.hasNext()){				
					String cas = case_ite.next();								
					new_entry += "(" + cas + ")|";

				} // while
				new_entry = new_entry.substring(0, new_entry.length() - 1); 
				System.out
				.println("SBMLXpathParser.FunctionTerm.getLogicalFunction(): final new_entry = " + new_entry);
				logical_function += new_entry + op_symbol;	

			} // while
			logical_function = logical_function.substring(0, logical_function.length() - 1);
			logical_function = "(" + logical_function + ")";
			return logical_function;
		}
		
		@Override
		public String toString() {

			String result = op + "\n";
			for (int i = 0; i < conditionList.size(); i++) {
				result += "|-- " + conditionList.get( i) + "\n";
			}			
			result += this.getLogicalFunction();			
			return result;			
		}
		
	} // class FunctionTerm

}