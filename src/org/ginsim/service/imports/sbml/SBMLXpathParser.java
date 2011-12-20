package org.ginsim.service.imports.sbml;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.BooleanParser;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.notification.resolvable.resolution.InvalidFunctionResolution;
import org.jdom.Attribute;
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

/**
 * This class allow to parse a SBML file. It use the Java XPath API and Jaxen library to manage the namespaces
 * of this SBML file. It also use the JSci API (a science API for Java) mainly the "JSci.mathml" package to parse
 * data located in every <fonctionTerm> element of SBML file.
 * @author Guy-Ross ASSOUMOU
 *
 */

public final class SBMLXpathParser {
	
	/** Creates a new instance of SbmlXpathParser */
	private RegulatoryGraph graph;
	protected File filePath;
	private String s_nodeOrder = "";
	private RegulatoryNode vertex = null;
	public RegulatoryEdge edge = null;
	private int vslevel = 0;

	private HashMap<String, RegulatoryEdge> m_edges = new HashMap<String, RegulatoryEdge>();
	static Pattern pattern;

	private Hashtable<RegulatoryNode, Hashtable<String, Vector<String>>> values;
	private Vector<String> v_function;

	public SBMLXpathParser() {
	}

	/**
	 * Parse a SBML file from a given file.
	 * @param filename
	 */
	public SBMLXpathParser(String filename) {

		this.filePath = new File(filename);
		this.graph = GraphManager.getInstance().getNewGraph();
		values = new Hashtable<RegulatoryNode, Hashtable<String, Vector<String>>>();
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
			document = sxb.build(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
		TransitionList transListClass = null;
		
		QualitativeSpeciesList qualSpeciesListClass = null;

		try {
			
			/** initialization of the root element. **/			
			Element racine = document.getRootElement();
			/**
			 * we need to declare every namespace of a file to avoid errors 
			 * when we will try to retrieve elements belonging at this namespace 
			 **/
			Namespace namespace1 = Namespace.getNamespace("sbml",
					"http://www.sbml.org/sbml/level3/version1/core");
			
			/** Allow to get the model ID. */
			XPath xpa1 = XPath.newInstance("//sbml:model");
			
			/** add this namespace (namespace1) from xpath namespace list **/ 		 
			xpa1.addNamespace(namespace1);

			Namespace namespace = Namespace.getNamespace("qual",
					"http://www.sbml.org/sbml/level3/version1/qual/version1");
			
			/** Search the list of species.**/
			XPath xpathQualSpecies = XPath.newInstance("//qual:listOfQualitativeSpecies");
			xpathQualSpecies.addNamespace(namespace);

			/** Search the transitions list **/

			XPath xpathTransition = XPath.newInstance("//qual:listOfTransitions");
			xpathTransition.addNamespace(namespace);
			
			/**
			 *  Get for model id
			 */
			List<Element> modelList = xpa1.selectNodes(racine);
			Element model = modelList.get(0);
			String modelId = model.getAttributeValue("id");
			
			if(modelId != null) {
				graph.setGraphName(modelId);
			}

			
			/** retrieves all nodes corresponding to the path:/model/listOfQualitativeSpecies. **/
			
			// Getting a transition list.
			List<Element> transList = xpathTransition.selectNodes(racine);
			Element transContent = transList.get(0);
			List<Element> transListElements = transContent.getChildren();
			
			// getting a qualitativeSpecies list.
			List<Element> qualSpeciesList = xpathQualSpecies.selectNodes(racine);
			
			/**
			 * Get listOfQualitativeSpecies <Element>
			 * allSpecies <Element> is a similar to listOfQualitativeSpecies.
			 */

			Element allSpecies = qualSpeciesList.get(0);
			
			/**
			 * speciesList contain every qualitativeSpecies <Element>.
			 */
			List<Element> speciesList = allSpecies.getChildren();

			
			
	//*************  dealing a qualitativeSpecies list  ***********************
			
			/** to retrieve species data **/

			qualSpeciesListClass = new QualitativeSpeciesList();
			
			/**
			 * A vector for nodeOrder
			 */
			List<RegulatoryNode> v_nodeOrder = new ArrayList<RegulatoryNode>();
			
			for(int k=0; k<speciesList.size(); k++) {
				
				Element obElement = speciesList.get(k);
				QualitativeSpecies qualClass = new QualitativeSpecies();
				
				Attribute att_id = obElement.getAttribute( "id", namespace);
				if( att_id != null){
					qualClass.setId( att_id.getValue());	
				}
				
				Attribute att_name = obElement.getAttribute( "name", namespace);
				if( att_name != null){
					qualClass.setName( att_name.getValue());	
				}
				
				Attribute att_compartment = obElement.getAttribute( "compartment", namespace);
				if( att_compartment != null){
					qualClass.setCompartment( att_compartment.getValue());	
				}
				
				Attribute att_maxlevel = obElement.getAttribute( "maxLevel", namespace);
				if( att_maxlevel != null){
					qualClass.setMaxLevel( att_maxlevel.getValue());	
				}
				
				Attribute att_init = obElement.getAttribute( "initialLevel", namespace);
				if( att_init != null){
					qualClass.setInitialLevel( att_init.getValue());	
				}
				
				Attribute att_bound = obElement.getAttribute( "boundaryCondition", namespace);
				if( att_bound != null){
					qualClass.setBoundaryCondition( att_bound.getValue());	
				}
				
				Attribute att_constant = obElement.getAttribute( "constant", namespace);
				if( att_constant != null){
					qualClass.setConstant( att_constant.getValue());	
				}

				/** we need construct a string character with different nodes from the graph **/
				s_nodeOrder += qualClass.getId() + " ";
				
				if (s_nodeOrder == null) {
					throw new JDOMException("missing nodeOrder");
				}
				
				RegulatoryNode node = new RegulatoryNode(qualClass.getId(), graph);
				v_nodeOrder.add(node);
				qualSpeciesListClass.getListOfQualitativeSpecies().add(qualClass);

				/** 
				 * set a node with a species data 
				 */
				byte maxval; 
				
				if(qualClass.getMaxLevel() == null) {
					maxval = 1;
				}else {
					maxval = (byte) Integer.parseInt(qualClass.getMaxLevel());
				}
				
				vertex = graph.addNewNode(qualClass.getId(), qualClass.getName(), maxval);
				vertex.getV_logicalParameters().setUpdateDup(false);
				
				String s_basal = qualClass.getBasevalue();
				
				if(null != s_basal)
				{
					try {
						
						byte basevalue = (byte)Integer.parseInt(s_basal);
						
						if(basevalue != 0)
						{
							vertex.addLogicalParameter(new LogicalParameter(basevalue), true);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				
				String input = qualClass.getBoundaryCondition();			
				if (null != input) {
					vertex.setInput(input.equalsIgnoreCase("true") || input.equals("1"),
							graph);
				}
				
				/**
				 * add this node in a node collection 
				*/
				
				values.put(vertex, new Hashtable<String, Vector<String>>());
											
			} // premier for
			graph.setNodeOrder(v_nodeOrder);
			
			
			//*****************   dealing a transition list   ********************//
			
			Transition transitionClass = null;
			transListClass = new TransitionList();
			
			for(int i=0; i<transListElements.size(); i++) {
				
				/**
				 *  Get a transition <Element>
				 */
				Element transition = transListElements.get(i);
				
				/**
				 * This Map contains every input name with its sign.
				 */
				HashMap<String, String> intput_to_sign = new HashMap<String, String>();
				
			/**
			 * get a transition Id
			 */
				String trans_Id = transition.getAttributeValue("id", namespace); 
				transitionClass = new Transition(trans_Id);
				
				/**
				 * Get Attributes for one transition <element>.
				 */
				List<Element> transChildren = transition.getChildren(); // = listOfInputs + listOfOutputs + listOfFunctionTerms
				
				for(int ind = 0; ind < transChildren.size(); ind++) {
					
					Element transitionChild = transChildren.get(ind); // listOfInputs ou listOfOutputs ou listOfFunctionTerms
					
					if("listOfInputs".equals(transitionChild.getName())) {

						List<Element> inpList = transitionChild.getChildren();
											
						for(int it=0; it<inpList.size(); it++) {
										
							Input inputClass = new Input();
							Element input = (Element) inpList.get(it);

							Attribute att_id = input.getAttribute( "id", namespace);
							if( att_id != null) {
								inputClass.setId( att_id.getValue());
							}
							
							Attribute att_qual = input.getAttribute( "qualitativeSpecies", namespace);
							if( att_qual != null) {
								inputClass.setQualitativeSpecies( att_qual.getValue());
							}
							
							Attribute att_tresh = input.getAttribute( "tresholdLevel", namespace);
							if( att_tresh != null) {
								inputClass.setTresholdLevel( att_tresh.getValue());
							}
							
							Attribute att_transEf = input.getAttribute( "transitionEffect", namespace);
							if( att_transEf != null) {
								inputClass.setTransitionEffect( att_transEf.getValue());
							}
							
							Attribute att_sign = input.getAttribute( "sign", namespace);
							if( att_sign != null) {
								inputClass.setSign( att_sign.getValue());
							}
							
							/**
							 * Add input <element> to input list
							 */
							transitionClass.addInput(inputClass);
							intput_to_sign.put( att_qual.getValue(), att_sign.getValue());
							
						} // for every <input>

					} //listOfInputs

					else if ("listOfOutputs".equals(transitionChild.getName())) {
					
						Output outputClass = new Output();
						List<Element> children_list = transitionChild.getChildren();
						for(int outInd=0; outInd<children_list.size(); outInd++) {
							Element output = children_list.get(outInd);
							
							/**
							 * Get attributes list for output <element>.
							 */
							
							Attribute att_qualit = output.getAttribute( "qualitativeSpecies", namespace);
							if( att_qualit != null) {
								outputClass.setQualitativeSpecies(att_qualit.getValue());
							}
							
							Attribute att_transit = output.getAttribute( "transitionEffect", namespace);
							if( att_transit != null) {
								outputClass.setTransitionEffect(att_transit.getValue());
							}
							
						}
						
						transitionClass.setOutput(outputClass);	
						createEdges( intput_to_sign, outputClass.getQualitativeSpecies());
					}
					
					else if("listOfFunctionTerms".equals(transitionChild.getName()) || "listOfFunctionTerms".equals(null)) {

						List<Element> functionTChildren = transitionChild.getChildren();
						String outputName = transitionClass.getOutput().getQualitativeSpecies();

						for(int fctIndex = 0; fctIndex < functionTChildren.size(); fctIndex++ ) { 
							
							/**
							 * Get every functionTerm <Element>.
							 */
							Element child = functionTChildren.get(fctIndex);
							String fctResultLevel = null;
							
							/**
							 * Check defaultTerm value
							 */
							if("defaultTerm".equals(child.getName())) {
								List<Attribute> fctDftAttribList = child.getAttributes();
								if("resultLevel".equals(fctDftAttribList.get(0).getName())) {
									String dftRsl = fctDftAttribList.get(0).getValue();
									byte dft_value = (byte)Integer.parseInt(dftRsl);
									
									
									  	if(!(dftRsl.equals("0"))){
										for (Enumeration<RegulatoryNode> enumvertex = values.keys(); enumvertex.hasMoreElements();) 
										{
											vertex = enumvertex.nextElement();
											String vertexName = vertex.toString();
											
											if(outputName.equals(vertexName))
											{								
												vertex.addLogicalParameter(new LogicalParameter(dft_value), true); 
											}
										}
									}
									
								} // if								
							} // if
							
							v_function = new Vector<String>();	
							
							if("functionTerm".equals(child.getName())) {
								List<Attribute> fctAttribList = child.getAttributes();	
								
								/**
								 * Get <functionTerm> resultLevel attribute
								 */
								List<Attribute> fctAt = child.getAttributes();
								fctResultLevel = fctAt.get(0).getValue();
							
								FunctionTerm function_term = deal( child);
															
								if( function_term != null) {
									addEdgesToMutliEdges( function_term, getNodeId(trans_Id), intput_to_sign, graph);
									v_function.addElement( function_term.getLogicalFunction());
								}
 
								for (Enumeration<RegulatoryNode> enumvertex = values.keys(); enumvertex.hasMoreElements();) 
								{
									vertex = enumvertex.nextElement();
									String vertexName = vertex.getId();
									
									if(outputName.equals(vertexName))
									{	
										values.get(vertex).put(fctResultLevel, v_function);	
									}
								}
							} // functionTerm
						} // for
					} // listOfFunctionTerms
				} // transition <element>
			} // listOfTransition			
		} // try			
		catch (JDOMException e) { 
			// An error occurred while parsing the document
			e.printStackTrace();
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
		
		Iterator<RegulatoryNode> it = graph.getNodeOrder().iterator();
		while (it.hasNext()) {
			RegulatoryNode vertex = it.next();
			vertex.getV_logicalParameters().cleanupDup();
		}
	} // void parse(File filePath)
	
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

		List<Element> rootConv = root.getChildren();
		Element math = rootConv.get(0);
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
	
	/**
	 * Create a new RegulatoryMultiEdge between the given output node and the given input nodes
	 * 
	 * @param input_node_names the list of the name of the on,put node (with the edge sign)
	 * @param output_node_name the name of the output node
	 */
	private void createEdges( HashMap<String, String> input_node_names, String output_node_name){
		
		RegulatoryNode output_node = graph.getNodeByName( output_node_name);
		
		if( output_node != null){
			for( String input_node_name : input_node_names.keySet()){
				
				RegulatoryNode input_node = graph.getNodeByName( input_node_name);
				if( input_node != null){
					String sign_code = input_node_names.get( input_node_name);
					RegulatoryEdgeSign sign;
					if ( "negative".equals(sign_code)){
						sign = RegulatoryEdgeSign.NEGATIVE;
					}
					else if ( "positive".equals(sign_code)){
						sign = RegulatoryEdgeSign.POSITIVE;
					}
					else{
						sign = RegulatoryEdgeSign.UNKNOWN;
					}	
					
					RegulatoryMultiEdge new_me = graph.addEdge( input_node, output_node, sign);
					
					if( new_me != null){
						new_me.rescanSign( graph);
					}
				}
			}
		}
	}
	
	
	/** Creating the multiEdges described in the given Functionterm 
	 * @param l_condition : a list of conditions for every transition 
	 * @param function_term : an object that contains a list of conditions for every 
     *        <functionTerm> element of a <transition> tag (see the sbml file).
	 * @param node_to_id : to identify a regulatory node target 
	 * @param input_to_sign: a map that contains every regulatory node source and his sign. */
	
	private void addEdgesToMutliEdges( FunctionTerm function_term, String node_to_id, HashMap<String, String> input_to_sign, RegulatoryGraph graph){		
		
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
				RegulatoryEdgeSign sign;
				if ( "negative".equals(sign_code)){
					sign = RegulatoryEdgeSign.NEGATIVE;
				}
				else if ( "positive".equals(sign_code)){
					sign = RegulatoryEdgeSign.POSITIVE;
				}
				else{
					sign = RegulatoryEdgeSign.UNKNOWN;
				}	
				Vector<String> list_cases = expression.getAllCases();
				
				for( int i = 0; i < list_cases.size(); i++) {
					String exp = list_cases.get(i);
					int index = exp.indexOf(":");
					if( index >= 0 && index < exp.length()-1) {
						byte threshold = (byte) Integer.parseInt( exp.substring( index+1));
						try{
							edge = graph.addNewEdge( node_from_id, node_to_id, threshold, sign);
							m_edges.put(sign_code, edge);
							edge.me.rescanSign(graph);
							graph.getEdgeAttributeReader().setEdge(edge.me);
						}
						catch (GsException gs_exception) {
							LogManager.error( "Unable to create new edge between vertices '" + node_from_id + "' and '" + node_to_id + "' : one of the vertex was not found in the graph");
							LogManager.error( gs_exception);
						}
					}
				}
			}
		}
	}
	
	
	/** This class parse a JSci String that correspond to all data located in <math> element from every
	 *  <functionTerm> element of a <transition> tag. */
	
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
		
		/**
		 * This method delete an "&" operator located in a String.
		 * for instance if the string is like : "CycD>=1&",
		 * a returned list will be : " [CycD>=1,... ]".
		 * @param code
		 * @return a list that contain strings without "&" operators.
		 */
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
					
			Pattern p = Pattern.compile( "\\(input_[^\\)]*_(\\d)\\)");
			Matcher m = p.matcher( code);
			
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				
				m.appendReplacement(sb, "(" + m.group(1) + ")");
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
	
	
	
	/** To get node ID corresponding to the current <transition> element **/
	public String getNodeId(String transId) {	
		String nodeName = null;
		int new_index;

			new_index = transId.indexOf( "_", 0);
			if( new_index >=0){
				Pattern pattern = Pattern.compile("tr_(.*)");
				Matcher matcher = pattern.matcher(transId);
				if(matcher.matches()){
					nodeName = matcher.group(1);
				}
			}
			else{			
				nodeName = transId;
			}
			
		return nodeName;
		
	}


	/** To place every node in the graph **/
	private void placeNodeOrder() {
		Vector<RegulatoryNode> v_order = new Vector<RegulatoryNode>();
		String[] t_order = s_nodeOrder.split(" ");
		for (int i = 0; i < t_order.length; i++) {
			RegulatoryNode vertex = (RegulatoryNode) graph.getNodeByName(t_order[i]);
			if (vertex == null) {
				// ok = false;
				break;
			}
			v_order.add(vertex);
		}
		if (v_order.size() != graph.getNodeCount()) {
			// error
			GUIMessageUtils.openErrorDialog("incoherent nodeOrder, not restoring it");
		} else {
			graph.setNodeOrder(v_order);
		}
	}

	/** To parse each logical function **/
	@SuppressWarnings("unchecked")
	private void parseBooleanFunctions() {
		Collection<RegulatoryMultiEdge> allowedEdges;
		RegulatoryNode vertex;
		String value, exp;
		try {
			for (Enumeration<RegulatoryNode> enu_vertex = values.keys(); enu_vertex.hasMoreElements();) {
				vertex = enu_vertex.nextElement();
				allowedEdges = graph.getIncomingEdges(vertex);
				if (allowedEdges.size() > 0) {
					for (Enumeration<String> enu_values = values.get(vertex).keys(); enu_values
							.hasMoreElements();) {
						value = (String) enu_values.nextElement();
						for (Enumeration<String> enu_exp = values.get(vertex)
								.get(value).elements(); enu_exp.hasMoreElements();) {
							exp = (String) enu_exp.nextElement();
							addExpression(Byte.parseByte(value), vertex, exp);
						}
					}
					vertex.getInteractionsModel().parseFunctions(); 
					if (vertex.getMaxValue() + 1 == values.get(vertex).size()) {
						((TreeElement) vertex.getInteractionsModel().getRoot()).setProperty(
								"add", new Boolean(false));
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addExpression(byte val, RegulatoryNode vertex, String exp) {
		try {
			BooleanParser tbp = new BooleanParser(graph.getIncomingEdges(
					vertex));
			TreeInteractionsModel interactionList = vertex.getInteractionsModel();
			if (!tbp.compile(exp, graph, vertex)) {
				
	        	Object[] data = new Object[3];
	        	data[0] = new Short(val);
	        	data[1] = vertex;
	        	data[2] = exp;
	        	
	        	NotificationManager.publishResolvableWarning( graph, "Invalid formula : " + exp, graph, data, new InvalidFunctionResolution());
			} else {
				interactionList.addExpression(val, vertex, tbp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
/**
 * @return a regulatory graph that corresponds to data of the SBML file
 */
	public RegulatoryGraph getGraph() {
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

	
	/** <p> An object Expression represent the contain of last "single" <apply> element.
	 * i.e : <apply>
	 *         <geq/>
	 *           <ci>nodeId</ci> 
	 *           <cn>thresholdValue</cn> 
	 *       </apply>
	 *  </p>     
	 ** @author Guy-Ross ASSOUMOU
	 **/	
 class Expression {
	 	/** The node id */	 
		private String node;
		
		/** The operator in this apply element, i.e : <geq/>. */
		private String operator;
		
		/** The threshold value in this apply element. */
		private int minvalue;
		
		/** The maxvalue of this <input> element (the regulatory node identified by the "node id"). */
		private int maxvalue;

		
		/* **************** CONSTRUCTORS ************/	
		
		public Expression() {
			this.node ="";
			this.operator = "";
			this.minvalue = 0;
			this.maxvalue = 0;
		}		

		/** To create an Expression Object with the maxvalue of every regulatory node of the graph. 
		 * @param str : a String that contains a needed data for an expression
		 * For instance : str = "G0<1", where G0 is a node id and.
		 * The caracter "<" is an operator and "1" is a threshold value*/
		
		public Expression(String str, RegulatoryGraph graph) {
			
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
					this.node ="";
					this.operator = "";
					this.minvalue = 0;
					this.maxvalue = 0;
				}
			}
			
			/** To set a maxvalue for a given regulatory node */
			RegulatoryNode vertex = (RegulatoryNode) graph.getNodeByName(node);
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
				for( int i = minvalue; i <= maxvalue; i++){
					String cas = node + ":" + i;
					cases.add( cas);
				}
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
  *  i.e : <apply>
  *  		  <and/>
  *           <apply>
  *	            <geq/>
  *              <ci>node_id1</ci> 
  *	             <cn>threshold_value1</cn> 
  *	          </apply>        
  *	          <apply>
  *	            <lt/>
  *	             <ci>node_id2</ci> 
  *	             <cn>threshold_value2</cn> 
  *	          </apply>
  *	       </apply>
  ** @author Guy-Ross ASSOUMOU
  **/
 
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
					
					/** Duplicate the string previously construct with the precedent expressions in order
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
	
 
 /** A FunctionTerm object represent a collection of Conditions object.
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

		/**
		 * 
		 * @return a list of conditions
		 */
		public List<Condition> getConditionList() {
			return conditionList;
		}	
		/**
		 * add a condition to a list of conditions
		 * @param cond
		 */
		public void addCondition(Condition cond) {
			conditionList.add(cond);
			
		}
	
		/** @return a logical function that corresponds to the all
		 *  conditions list located in the <functionTerm> element of a transition.
		 * **/
		public String getLogicalFunction(){
			
			String logical_function = "";
			Iterator<Condition> cond_ite = conditionList.iterator();
			while( cond_ite.hasNext()){
				Condition cond = cond_ite.next();
				Vector<String> cond_cases = cond.getAllCases();
				String new_entry = "";
				Iterator<String> case_ite = cond_cases.iterator();
				
				/**
				 * Build the logical function corresponding to data of functionTerm element 
				 */
				
				while( case_ite.hasNext()){				
					String cas = case_ite.next();								
					new_entry += "(" + cas + ")|";

				} // while
				new_entry = new_entry.substring(0, new_entry.length() - 1); 
				logical_function += new_entry + op_symbol;	

			} // while
			
			/**
			 * Delete the last "|" (or operator) caracter.
			 */
			logical_function = logical_function.substring(0, logical_function.length() - 1);
			/**
			 * Surround the last logical function by parentheses.
			 */
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



/* **************** QUALITATIVESPECIESLIST CLASS ************/

class QualitativeSpeciesList {

private List<QualitativeSpecies> listOfQualitativeSpecies;

public QualitativeSpeciesList() {	

this.listOfQualitativeSpecies = new ArrayList<QualitativeSpecies>();
}

public List<QualitativeSpecies> getListOfQualitativeSpecies() {
return listOfQualitativeSpecies;
}

public void setListOfQualitativeSpecies(
	List<QualitativeSpecies> listOfQualitativeSpecies) {
this.listOfQualitativeSpecies = listOfQualitativeSpecies;
}	
}// QualitativeSpeciesList


/* **************** QUALITATIVESPECIES CLASS ************/

class QualitativeSpecies {
 
private String id;
private String metaid;
private String sboTerm;
private String name;
private String compartment;
private String maxLevel;
private String basevalue;
private String initialLevel;
private String boundaryCondition;
private String constant;

public QualitativeSpecies() {
	
	this.name = null;
	this.id= null;
	this.metaid = null;
	this.sboTerm = null;
	this.compartment = null;
	this.maxLevel = null;
	this.basevalue = null;
	this.initialLevel = null;
	this.boundaryCondition = null;
	this.constant = null;
}

public String getId() {
	return id;
}

public void setId(String idValue) {
	this.id = idValue;
}

public String getMetaid() {
	return metaid;
}

public void setMetaid(String metaid) {
	this.metaid = metaid;
}

public String getSboTerm() {
	return sboTerm;
}

public void setSboTerm(String sboTerm) {
	this.sboTerm = sboTerm;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getCompartment() {
	return compartment;
}

public void setCompartment(String compartment) {
	this.compartment = compartment;
}

public String getMaxLevel() {
	return maxLevel;
}

public void setMaxLevel(String maxLevel) {
	this.maxLevel = maxLevel;
}

public String getInitialLevel() {
	return initialLevel;
}

public void setInitialLevel(String initialLevel) {
	this.initialLevel = initialLevel;
}

public String getBasevalue() {
	return basevalue;
}

public void setBasevalue(String basevalue) {
	this.basevalue = basevalue;
}

public String getBoundaryCondition() {
	return boundaryCondition;
}

public void setBoundaryCondition(String boundaryCondition) {
	this.boundaryCondition = boundaryCondition;
}

public String getConstant() {
	return constant;
}

public void setConstant(String constant) {
	this.constant = constant;
}

public String toString() {
	
	return "id : " + this.getId() + 
			" \nMetaid : " + this.getMetaid() +  
			" \nSboTerm : " + this.getSboTerm() +  
			" \nName : " + this.getName() +  
			" \nCompartment : " + this.getCompartment() +
			" \nBoudarycondition : " + this.getBoundaryCondition() +
			" \nInitialLevel : " + this.getInitialLevel() +
			" \nMaxLevel : " + this.getMaxLevel()+
			" \nBasevalue: " + this.getBasevalue()+
			" \nConstantV : " + this.getConstant();
	
}	
}

/* **************** INPUT CLASS ************/

class Input {
 
 private String id;
 private String qualitativeSpecies;
 private String tresholdLevel;
 private String transitionEffect;
 private String sign;
 	 
public Input() {
	this.id = null;
	this.qualitativeSpecies = null;
	this.tresholdLevel = null;
	this.transitionEffect = null;
	this.sign = null;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getQualitativeSpecies() {
	return qualitativeSpecies;
}

public void setQualitativeSpecies(String qualitativeSpecies) {
	this.qualitativeSpecies = qualitativeSpecies;
}

public String getThresholdLevel() {
	return tresholdLevel;
}

public void setTresholdLevel(String tresholdLevel) {
	this.tresholdLevel = tresholdLevel;
}

public String getTransitionEffect() {
	return transitionEffect;
}

public void setTransitionEffect(String transitionEffect) {
	this.transitionEffect = transitionEffect;
}

public String getSign() {
	return sign;
}

public void setSign(String sign) {
	this.sign = sign;
}

public String toString() {
	return "id : " + id +
		   " \nqualitativeSpecies : " + qualitativeSpecies +
		   " \ntresholdLevel      : " + tresholdLevel +
		   " \ntransitionEffect   : " + transitionEffect +
		   " \nsign : " + sign ;
}
}


/* **************** OUTPUT CLASS ************/

class Output {
 
 private String qualitativeSpecies;
 private String transitionEffect;
 
public Output() {		
	this.qualitativeSpecies = null;
	this.transitionEffect = null;
}

public String getQualitativeSpecies() {
	return qualitativeSpecies;
}

public void setQualitativeSpecies(String qualitativeSpecies) {
	this.qualitativeSpecies = qualitativeSpecies;
}

public String getTransitionEffect() {
	return transitionEffect;
}

public void setTransitionEffect(String transitionEffect) {
	this.transitionEffect = transitionEffect;
}	 

public String toString() {
	return  "qualitativeSpecies : " + qualitativeSpecies +		  
		    " \ntransitionEffect   : " + transitionEffect;
}

} // Output



/* **************** TRANSITION CLASS ************/

class Transition {
 
private String id;	 
private List<Input> listOfInputs;
private Output output;
 
public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public Transition(String id) {
	this.id = id;
	this.listOfInputs = new ArrayList<Input>();
	this.output = null;
}

public List<Input> getListOfInputs() {
	return listOfInputs;
}
public void setListOfInputs(List<Input> listOfInputs) {
	this.listOfInputs = listOfInputs;
}

public void addInput( Input input){
	this.listOfInputs.add( input);
}

public Output getOutput() {
	return output;
}

public void setOutput(Output output) {
	this.output = output;
}

} // Transition


/* **************** TRANSITIONLIST CLASS ************/

class TransitionList {
 
 private List<Transition> listTransition;

public TransitionList() {
	this.listTransition = new ArrayList<Transition>();
}

public List<Transition> getListTransition() {
	return listTransition;
}

public void setListTransition(List<Transition> listTransition) {
	this.listTransition = listTransition;
}		
} // TransitionList

}
