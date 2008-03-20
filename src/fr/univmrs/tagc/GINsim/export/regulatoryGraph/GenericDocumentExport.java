package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.annotation.AnnotationLink;
import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitStateTableModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.document.DocumentStyle;
import fr.univmrs.tagc.common.document.DocumentWriter;
import fr.univmrs.tagc.common.document.OOoDocumentWriter;
import fr.univmrs.tagc.common.document.XHTMLDocumentWriter;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * Generic export for documents.
 * 
 * 
 */
public class GenericDocumentExport extends GsAbstractExport {
	static public Vector v_format = new Vector();
	static {
		v_format.add(new GenericDocumentFormat(XHTMLDocumentWriter.class, "xHTML", new String[] {"html"}, "xHTML files (.html)", ".html"));
		v_format.add(new GenericDocumentFormat(OOoDocumentWriter.class, "OpenOffice.org", new String[] {"odt"}, "OpenOffice.org files (.odt)", ".odt"));
	}

	private GsExportConfig config = null;
	protected DocumentWriter doc = null;
	protected Class documentWriterClass;

	private GsRegulatoryGraph graph;
	private List nodeOrder;
	private int len;
	
    public GenericDocumentExport() {
		id = "Documentation";
    }
    
    protected static void addSubFormat(Class documentWriterClass, String id, String[] filter, String filterDescr, String extension) {
    	v_format.add(new GenericDocumentFormat(documentWriterClass, id, filter, filterDescr, extension));
    }
   
	public Vector getSubFormat() {
		return v_format;
	}
   
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_Generic", "STR_Generic_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		try {
			System.out.println(config.getFilename());
			this.doc = (DocumentWriter) documentWriterClass.newInstance();
			this.doc.setOutput(new FileOutputStream(new File(config.getFilename())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			long l = System.currentTimeMillis();
			run();
			System.out.println("Generic export: done in "+(System.currentTimeMillis()-l)+"ms");
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
		// TODO: use a more precise config
		writeDocument();
	}

	private void writeDocument() throws IOException {
		doc.startDocument();
		doc.openHeader(1, "Description of the model \"" + graph.getGraphName() + "\"", null);
				
		//Graph annotation
		doc.openHeader(2, "Annotation", null);
		writeAnnotation(graph.getAnnotation());
		
		// all nodes with comment and logical functions
		if (true) {
			doc.openHeader(2, "Nodes", null);
			writeLogicalFunctionsTable();
		}
		
		// initial states
		if (true) {
			doc.openHeader(2, "Initial States", null);
			writeInitialStates();
		}
		// mutant description
		if (true) {
			doc.openHeader(2, "Dynamical Behaviour", null);
			// TODO: export mutants as well
		}

		doc.close();//close the document		
	}

	private void writeInitialStates() throws IOException {
		GsInitialStateList initStates = (GsInitialStateList) graph.getObject(
				GsInitialStateManager.key, false);
		if (initStates != null && initStates.getNbElements(null) > 0) {
			GsInitStateTableModel model = new GsInitStateTableModel(nodeOrder, null, initStates, false);
			String[] t_cols = new String[len+1];
			for (int i=0 ; i<=len ; i++) {
				t_cols[i] = "";
			}
			doc.openTable("initialStates", null, t_cols);
			doc.openTableRow();
			doc.openTableCell("Name");
			for (int i = 0; i < len; i++) {
				doc.openTableCell(""+nodeOrder.get(i));
			}
			for ( int i=0 ; i< initStates.getNbElements(null) ; i++ ) {
				doc.openTableRow();
				doc.openTableCell(""+model.getValueAt(i, 0));
				for (int j = 0; j < len; j++) {
					doc.openTableCell(""+model.getValueAt(i, j+2));
				}
			}
			doc.closeTable();
		}
	}

	private void writeLogicalFunctionsTable() throws IOException {
		doc.openTable(null, null, new String[] { "s1", "s2", "s3", "s4" });
		doc.openTableRow();
		doc.openTableCell("ID");
		doc.openTableCell("Val");
		doc.openTableCell("Logicial function");
		doc.openTableCell("Comment");
		
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
			doc.openTableRow();
			doc.openTableCell(1, nbrows, vertex.getId()); //ID
			int currentValue = 0;
			if (nbrows > 0) {
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
			doc.openTableCell(1,nbrows, null);
			writeAnnotation(vertex.getAnnotation());
			doc.closeTableRow();
			
			// add the other functions
			if (nbrows > 1) {
				for (currentValue++ ; currentValue<t_val.length ; currentValue++) {
					if (t_val[currentValue] != null) {
						doc.openTableRow();
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
			if (lnk.getHelper() != null) {
				String s = lnk.getHelper().getLink(lnk.getProto(), lnk.getValue());
				if (s == null) {
					doc.openListItem(null);
					doc.writeText(lnk.toString());
					doc.closeListItem();
				} else {
					if (s == lnk.toString() && s.length() >= 50) {
						doc.openListItem(null);
						doc.addLink(s, s.substring(0, 45) + "...");
						doc.closeListItem();
					} else {
						doc.openListItem(null);
						doc.addLink(s, lnk.toString());
						doc.closeListItem();
					}
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
	 * Set the style for the document.
	 */
	private void setDocumentStyles() {
		DocumentStyle styles = doc.getStyles();
		styles.addStyle("L1");
		styles.addProperty(DocumentStyle.LIST_TYPE, "U");	
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

class GDExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;
   
    
	protected GDExportConfigPanel (GsExportConfig config, StackDialog dialog) {
//		setLayout( new FlowLayout(FlowLayout.LEFT));
//		JLabel label = new JLabel("File type");
//		add(label);
//		
//		//getting the formats ids
//		String [] formatIDs = new String[GenericDocumentExport.v_format.size()];
//		int i = 0;
//		Iterator it = GenericDocumentExport.v_format.iterator();
//		while (it.hasNext()) {
//			Map e = (Map) it.next();
//			formatIDs[i++] = (String)e.get(GenericDocumentExport.FORMAT_ID);
//		}
//		JComboBox format = new JComboBox(formatIDs);
//		add(format);
    }
}

class GenericDocumentFormat extends GenericDocumentExport {
	
	GenericDocumentFormat(Class documentWriterClass, String id, String[] filter, String filterDescr, String extension) {
		this.documentWriterClass = documentWriterClass;
		this.id = id;
		this.filter = filter;
		this.filterDescr = filterDescr;
		this.extension = extension;		
	}
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
		return null;
	}
	
}