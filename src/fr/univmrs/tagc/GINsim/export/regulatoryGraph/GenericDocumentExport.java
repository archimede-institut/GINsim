package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.action.stablestates.StableTableModel;
import org.ginsim.service.action.stablestates.StableStatesService;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.annotation.AnnotationLink;
import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitStateTableModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.document.DocumentStyle;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.GenericDocumentFormat;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * GenericDocumentExport is a plugin to export the documentation of a model into multiples document format.
 * 
 * It export using a documentWriter. You can add support for your own document writer using <u>addSubFormat</u>
 * 
 * @see DocumentWriter
 */
public class GenericDocumentExport extends GsAbstractExport {
	static public Vector v_format = new Vector();
	static {
		for (Iterator it = GenericDocumentFormat.getAllFormats().iterator(); it.hasNext();) {
			GenericDocumentFormat format = (GenericDocumentFormat) it.next();
			v_format.add(GenericDocumentExportFormat.createFrom(format));
		}
	}

	private GsExportConfig config = null;
	private DocumentExportConfig specConfig;
	protected DocumentWriter doc = null;
	protected Class documentWriterClass;

	private GsRegulatoryGraph graph;
	private List nodeOrder;
	private int len;
	
    public GenericDocumentExport() {
		id = "Documentation";
    }

    /**
     * get a vector of all the GenericDocumentFormat the genericDocument can use.
     */
	public Vector getSubFormat() {
		return v_format;
	}
   
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_Generic", "STR_Generic_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		this.specConfig = (DocumentExportConfig)config.getSpecificConfig();
		if (specConfig == null) {
			specConfig = new DocumentExportConfig();
			config.setSpecificConfig(specConfig);
		}
		try {
			this.doc = (DocumentWriter) documentWriterClass.newInstance();
			this.doc.setOutput(new File(config.getFilename()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			run();
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}		
	}

	public boolean needConfig(GsExportConfig config) {
		return true;
	}

	protected JComponent getConfigPanel(GsExportConfig config, StackDialog dialog) {
		return new GDExportConfigPanel(config, dialog);
	}

	
	protected synchronized void run() throws IOException {
		this.graph = (GsRegulatoryGraph) config.getGraph();
		nodeOrder = graph.getNodeOrder();
		len = nodeOrder.size();
		setDocumentProperties();
		setDocumentStyles();
		if (doc.doesDocumentSupportExtra("javascript")) {
			setJavascript();
		}
		writeDocument();
	}

	private void writeDocument() throws IOException {
		doc.startDocument();
		doc.openHeader(1, "Description of the model \"" + graph.getGraphName() + "\"", null);
				
		//Graph annotation
		doc.openHeader(2, "Annotation", null);
		writeAnnotation(graph.getAnnotation());
		doc.openParagraph(null);
		
		// FIXME: add back image to the documentation
		// doc.addImage(graph.getGraphManager().getImage(), "model.png");
		
		doc.closeParagraph();
		
		// all nodes with comment and logical functions
		if (true) {
			doc.openHeader(2, "Nodes", null);
			writeLogicalFunctionsTable(specConfig.putComment);
		}
		
		// initial states
		if (specConfig.exportInitStates) {
			doc.openHeader(2, "Initial States", null);
			writeInitialStates();
		}
		// mutant description
		if (specConfig.exportMutants) {
			doc.openHeader(2, "Mutants and Dynamical Behaviour", null);
			writeMutants();
		}

		doc.close();//close the document		
	}

	private void writeMutants() throws IOException {
		GsRegulatoryMutants mutantList = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
		StableStatesService stableSearcher = new StableStatesService(config.getGraph(), null, null);
		OmddNode stable;
		
		String[] cols;
		if (specConfig.searchStableStates && specConfig.putComment) {
			cols = new String[] {"", "", "", "", "", "", ""};
		} else if (specConfig.searchStableStates || specConfig.putComment){
			cols = new String[] {"", "", "", "", "", ""};
		} else {
			cols = new String[] {"", "", "", "", ""};
		}
		int nbcol = cols.length-1;
		doc.openTable("mutants", "table", cols);
		doc.openTableRow(null);
		doc.openTableCell("Mutants", true);
		doc.openTableCell("Gene", true);
		doc.openTableCell("Min", true);
        doc.openTableCell("Max", true);
        doc.openTableCell("Condition", true);
		if (specConfig.putComment) {
			doc.openTableCell("Comment", true);
		}
		if (specConfig.searchStableStates) {
			doc.openTableCell("Stable States", true);
		}
		
		StableTableModel model = new StableTableModel(nodeOrder);
		for (int i=-1 ; i<mutantList.getNbElements(null) ; i++) {
			GsRegulatoryMutantDef mutant = 
				i<0 ? null : (GsRegulatoryMutantDef)mutantList.getElement(null, i);
			
			if (specConfig.searchStableStates) {
				stableSearcher.setMutant(mutant);
				stable = stableSearcher.getStable();
				model.setResult(stable, graph);
			}
			int nbrow;
			Iterator it_multicellularChanges = null;
			if (i<0) { // wild type
				nbrow = 1;
				doc.openTableRow(null);
				doc.openTableCell(1, (model.getRowCount() > 0?2:1), "Wild Type", true);
				doc.openTableCell("-");
				doc.openTableCell("-");
                doc.openTableCell("-");
                doc.openTableCell("");
				if (specConfig.putComment) {
					doc.openTableCell("");
				}
			} else {
				if (!specConfig.multicellular) {
					nbrow = mutant.getNbChanges();
				} else {
					nbrow = mutant.getNbChanges();
					Map m_multicellularChanges = new HashMap();
					for (int c=0 ; c<nbrow ; c++) {
						String s = mutant.getName(c);
						// TODO: check that the mutant is indeed the same everywhere before doing so ?
						if (s.endsWith("1")) {
							m_multicellularChanges.put(s.substring(0, s.length()-1), 
									new int[] {mutant.getMin(c), mutant.getMax(c)});
						}
					}
					nbrow = m_multicellularChanges.size();
					it_multicellularChanges = m_multicellularChanges.entrySet().iterator();
				}
				if (nbrow < 1) {
					nbrow = 1;
				}
				doc.openTableRow(null);
				doc.openTableCell(1, (nbrow+(model.getRowCount() > 0?1:0)), mutant.getName(), true);
				if (mutant.getNbChanges() == 0) {
					doc.openTableCell("-");
					doc.openTableCell("-");
                    doc.openTableCell("-");
				} else if (it_multicellularChanges == null){
					doc.openTableCell(mutant.getName(0));
					doc.openTableCell(""+mutant.getMin(0));
					doc.openTableCell(""+mutant.getMax(0));
				} else {
					Entry e = (Entry)it_multicellularChanges.next();
					doc.openTableCell(e.getKey().toString());
					int[] t_changes = (int[])e.getValue();
					doc.openTableCell(""+t_changes[0]);
					doc.openTableCell(""+t_changes[1]);
				}
				if (mutant.getNbChanges() > 0) {
				    doc.openTableCell(mutant.getCondition(0));
				} else {
				    doc.openTableCell("");
				}
				if (specConfig.putComment) {
					doc.openTableCell(1, nbrow, "", false);
					writeAnnotation(mutant.getAnnotation());//BUG?
				}
			}
			
			if (specConfig.searchStableStates) {
				// the common part: stable states
				if (model.getRowCount() > 0) {
					doc.openTableCell(1, nbrow, model.getRowCount()+" Stable states", false);
				} else {
					doc.openTableCell(1, nbrow, "", false);
				}
			}

			// more data on mutants:
			if (mutant != null) {
				for (int j=1 ; j<nbrow ; j++) {
					if (it_multicellularChanges == null) {
						doc.openTableRow(null);
						doc.openTableCell(mutant.getName(j));
						doc.openTableCell(""+mutant.getMin(j));
						doc.openTableCell(""+mutant.getMax(j));
					} else {
						Entry e = (Entry)it_multicellularChanges.next();
						doc.openTableRow(null);
						doc.openTableCell(e.getKey().toString());
						int[] t_changes = (int[])e.getValue();
						doc.openTableCell(""+t_changes[0]);
						doc.openTableCell(""+t_changes[1]);
					}
                    doc.openTableCell(""+mutant.getCondition(j));
				}
			}
			
			// more data on stable states:
			if (specConfig.searchStableStates && model.getRowCount() > 0) {
				doc.openTableRow(null);
				doc.openTableCell(nbcol,1, null, false);
				
				doc.openList("L1");
				for (int k=0 ; k<model.getRowCount() ; k++) {
					doc.openListItem(null);
					boolean needPrev=false;
					String name = (String)model.getValueAt(k,0);
					if (name != null) {
						doc.writeText(name+": ");
					}
					for (int j=1 ; j<=len ; j++) {
						Object val = model.getValueAt(k,j);
						if (!val.toString().equals("0")) {
							String s = needPrev ? " ; " : "";
							needPrev = true;
							if (val.toString().equals("1")) {
								doc.writeText(s+nodeOrder.get(j-1));
							} else {
								doc.writeText(s+nodeOrder.get(j-1)+"="+val);
							}
						}
					}
					doc.closeListItem();
				}
				doc.closeList();
			}
		}
		doc.closeTable();
	}
	
	
	private void writeInitialStates() throws IOException {
		InitialStateList initStates = ((GsInitialStateList) graph.getObject(
				GsInitialStateManager.key, false)).getInitialStates();
		if (initStates != null && initStates.getNbElements(null) > 0) {
			GsInitStateTableModel model = new GsInitStateTableModel(null, initStates, false);
			String[] t_cols = new String[len+1];
			for (int i=0 ; i<=len ; i++) {
				t_cols[i] = "";
			}
			doc.openTable("initialStates", "table", t_cols);
			doc.openTableRow(null);
			doc.openTableCell("Name");
			for (int i = 0; i < len; i++) {
				doc.openTableCell(""+nodeOrder.get(i));
			}
			for ( int i=0 ; i< initStates.getNbElements(null) ; i++ ) {
				doc.openTableRow(null);
				doc.openTableCell(""+model.getValueAt(i, 0));
				for (int j = 2; j < model.getColumnCount(); j++) {
					doc.openTableCell(""+model.getValueAt(i, j));
				}
			}
			doc.closeTable();
		}
	}

	private void writeLogicalFunctionsTable(boolean putcomment) throws IOException {
		if (specConfig.putComment) {
			doc.openTable(null, "table", new String[] { "", "", "", "" });
		} else {
			doc.openTable(null, "table", new String[] { "", "", "" });
		}
		doc.openTableRow(null);
		doc.openTableCell("ID", true);
		doc.openTableCell("Val", true);
		doc.openTableCell("Logical function", true);
		if (putcomment) {
			doc.openTableCell("Comment", true);
		}
		
		for (Iterator it=graph.getNodeOrder().iterator() ; it.hasNext() ;) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			GsTreeInteractionsModel lfunc = vertex.getInteractionsModel();
			int nbval = 0;
			Object funcRoot = null;
			List[] t_val = new List[vertex.getMaxValue()+1];
			int nbrows = 0;
			if (lfunc != null) {
				funcRoot = lfunc.getRoot();
				nbval = lfunc.getChildCount(funcRoot);
				if (nbval == 0) {
					funcRoot = null;
				}
				// put all values from function
				for (int i=0 ; i<nbval ; i++) {
					GsTreeValue val = (GsTreeValue)lfunc.getChild(funcRoot, i);
					int v = val.getValue();
					if (lfunc.getChildCount(val) > 0) {
						t_val[v] = new ArrayList();
						t_val[v].add(val);
						nbrows++;
					}
				}
			}
			// and add logical parameters as well
			Iterator it_param = vertex.getV_logicalParameters().iterator(true);
			while (it_param.hasNext()) {
				GsLogicalParameter param = (GsLogicalParameter)it_param.next();
				if (!param.isDup()) {
					int v = param.getValue();
					if (t_val[v] == null) {
						t_val[v] = new ArrayList();
						nbrows++;
					}
					t_val[v].add(param);
				}
			}
			doc.openTableRow(null);
			doc.openTableCell(1, nbrows, vertex.getId(), true); //ID
			int currentValue = 0;
			if (nbrows > 0) {
				// TODO: put a "0" if nothing is defined
				for ( ; currentValue<t_val.length ; currentValue++) {
					if (t_val[currentValue] != null) {
						doWriteParameters(currentValue, t_val[currentValue], lfunc);
						break;
					}
				}
			} else {
				doc.openTableCell(null);//Values (empty)
				doc.openTableCell("no function");//function
			}
			if (putcomment) {
				doc.openTableCell(1,nbrows, null, false);
				writeAnnotation(vertex.getAnnotation());
			}
			doc.closeTableRow();
			
			// add the other functions
			if (nbrows > 1) {
				for (currentValue++ ; currentValue<t_val.length ; currentValue++) {
					if (t_val[currentValue] != null) {
						doc.openTableRow(null);
						doWriteParameters(currentValue, t_val[currentValue], lfunc);
						doc.closeTableRow();
					}
				}
			} 
		}		
		doc.closeTable();		
	}
	
	private void doWriteParameters(int value, List data, GsTreeInteractionsModel lfunc) throws IOException {
		doc.openTableCell(""+value); //Values
		doc.openTableCell(null); //logical function
		doc.openList("L1");
		for (Iterator it_all=data.iterator() ; it_all.hasNext() ; ) {
			Object o = it_all.next();
			if (o instanceof GsTreeValue) {
				int nbfunc = lfunc.getChildCount(o);
				for (int j=0 ; j<nbfunc ; j++) {
					Object func = lfunc.getChild(o, j);
					doc.openListItem(func.toString());
				}
			} else {
				doc.openListItem(o.toString());
			}
		}
		doc.closeList();
		doc.closeTableCell();
	}
	
	public void writeAnnotation(Annotation annotation) throws IOException {
		boolean hasLink = false;
		Iterator it = annotation.getLinkList().iterator();
		if (it.hasNext()) {
			hasLink = true;
			doc.openList("L1");
		}
		while (it.hasNext()) {
			AnnotationLink lnk = (AnnotationLink)it.next();
			String s_link;
			if (lnk.getHelper() != null) {
				s_link = lnk.getHelper().getLink(lnk.getProto(), lnk.getValue());
			} else {
				s_link = Tools.getLink(lnk.getProto(), lnk.getValue());
			}
			if (s_link == null) {
				doc.openListItem(null);
				doc.writeText(lnk.toString());
				doc.closeListItem();
			} else {
				if (s_link == lnk.toString() && s_link.length() >= 50) {
					doc.openListItem(null);
					doc.addLink(s_link, s_link.substring(0, 45) + "...");
					doc.closeListItem();
				} else {
					doc.openListItem(null);
					doc.addLink(s_link, lnk.toString());
					doc.closeListItem();
				}
			}
		}
		if (hasLink) {
			doc.closeList();
		}
		doc.openParagraph(null);
		String[] t = annotation.getComment().split("\n");
		for (int i = 0; i < t.length-1; i++) {
			doc.writeTextln(t[i]);
		}
		doc.writeText(t[t.length-1]);
		doc.closeParagraph();
	}

	/**
	 * import the javascript (DocumentExtra) from js file. 
	 * The javascript is use to allow the user to collapse/expand the stables states in the table.
	 * @throws IOException 
	 * 
	 */
	private void setJavascript() throws IOException {
		StringBuffer javascript = doc.getDocumentExtra("javascript");
		InputStream stream = Tools.getStreamForPath("/fr/univmrs/tagc/GINsim/resources/makeStableStatesClickable.js");
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String s;
		while ((s = in.readLine()) != null) {
			javascript.append(s);
			javascript.append("\n");
		}
	}
	/**
	 * Set the style for the document.
	 */
	private void setDocumentStyles() {
		DocumentStyle styles = doc.getStyles();
		styles.addStyle("L1");
		styles.addProperty(DocumentStyle.LIST_TYPE, "U");	
		styles.addStyle("table");
		styles.addProperty(DocumentStyle.TABLE_BORDER, new Integer(1));	
	}

	/**
	 * Set the properties (meta-information) for the document.
	 */
	private void setDocumentProperties() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//for dc:date
		doc.setDocumentProperties(new String[] {
				//DocumentWriter.META_AUTHOR,
				DocumentWriter.META_DATE, simpleDateFormat.format(new Date()).toString(),
				//DocumentWriter.META_DESCRIPTION, 
				DocumentWriter.META_GENERATOR, "GINsim",
				//DocumentWriter.META_KEYWORDS,
				DocumentWriter.META_TITLE, graph.getGraphName()
		});
	}
  		

}

class DocumentExportConfig implements GsInitialStateStore {
    Map m_init = new HashMap();
    Map m_input = new HashMap();

	boolean exportInitStates = true;
	boolean exportMutants = true;
	boolean searchStableStates = true;
	boolean putComment = true;
	// set to true to avoid generating redondant things for multicellular models
	boolean multicellular = false;
	
    public Map getInitialState() {
        return m_init;
    }
    public Map getInputState() {
        return m_input;
    }
	
}

class GDExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;
    
    protected DocumentExportConfig cfg;
    JCheckBox cb_stable, cb_init, cb_mutants, cb_multicellular, cb_comment;
    
	protected GDExportConfigPanel (GsExportConfig config, StackDialog dialog) {
		cfg = (DocumentExportConfig)config.getSpecificConfig();
		if (cfg == null) {
			cfg = new DocumentExportConfig();
			config.setSpecificConfig(cfg);
		}
    	GsInitialStatePanel initPanel = new GsInitialStatePanel(dialog, config.getGraph(), false);
    	initPanel.setParam(cfg);

    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        c.weightx = c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(initPanel, c);
        
        c.weightx = c.weighty = 0;
        ChangeListener listener = new MyListener();

        cb_stable = new JCheckBox("stable");
        cb_stable.addChangeListener(listener);
        cb_stable.setSelected(cfg.searchStableStates);
        c.gridy++;
        add(cb_stable, c);
        cb_init = new JCheckBox("initial states");
        cb_init.addChangeListener(listener);
        cb_init.setSelected(cfg.exportInitStates);
        c.gridy++;
        add(cb_init, c);
        cb_mutants = new JCheckBox("mutants");
        cb_mutants.addChangeListener(listener);
        cb_mutants.setSelected(cfg.exportMutants);
        c.gridy++;
        add(cb_mutants, c);
        cb_multicellular = new JCheckBox("multicellular");
        cb_multicellular.addChangeListener(listener);
        cb_multicellular.setSelected(cfg.multicellular);
        c.gridy++;
        add(cb_multicellular, c);
        cb_comment = new JCheckBox("comments");
        cb_comment.addChangeListener(listener);
        cb_comment.setSelected(cfg.putComment);
        c.gridy++;
        add(cb_comment, c);
    }
	class MyListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JCheckBox src = (JCheckBox)e.getSource();
            if (src == cb_stable) {
                cfg.searchStableStates = src.isSelected();
            } else if (src == cb_comment) {
                cfg.putComment = src.isSelected();
            } else if (src == cb_multicellular) {
                cfg.multicellular = src.isSelected();
            } else if (src == cb_init) {
                cfg.exportInitStates = src.isSelected();
            } else if (src == cb_mutants) {
                cfg.exportMutants = src.isSelected();
            }
        }
	}
}

/**
 * This class contain the informations 
 */
class GenericDocumentExportFormat extends GenericDocumentExport {
	/**
	 * Define a new generic document format.
	 * @param documentWriterClass : The DocumentWriter sub-class for the format
	 * @param id : The name of the format (for the dropdown menu)
	 * @param filter : an array of filter for the file extension the format can overwrite
	 * @param fillterDescr : a description
	 * @param extention : the extension to add to the exported file
	 */
	public GenericDocumentExportFormat(Class documentWriterClass, String id, String[] filter, String filterDescr, String extension) {
		this.documentWriterClass = documentWriterClass;
		this.id = id;
		this.filter = filter;
		this.filterDescr = filterDescr;
		this.extension = extension;		
	}
	
	public static GenericDocumentExportFormat createFrom(GenericDocumentFormat format) {
		return new GenericDocumentExportFormat(format.documentWriterClass, format.id, format.extensionArray, format.filterDescr, format.defaultExtension);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
		return null;
	}
	
}
