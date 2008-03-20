package fr.univmrs.tagc.common.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.annotation.AnnotationLink;
import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.common.GsException;
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
		
		setDocumentProperties();
		setDocumentStyles();
		writeDocument();
	}

	private void writeDocument() throws IOException {
		doc.startDocument();
		doc.openHeader(1, "Description of the model \"" + graph.getGraphName() + "\"", null);
				
		//Graph annotation
		doc.openHeader(2, "Annotation", null);
		writeAnnotation(graph.getAnnotation());
		
		// all nodes with comment and logical functions
		doc.openHeader(2, "Nodes", null);
		writeLogicalFunctionsTable();

		doc.close();//close the document		
	}

	private void writeLogicalFunctionsTable() throws IOException {
		doc.openTable(null, null, null);
		doc.openTableRow();
		doc.openTableCell("ID");
		doc.openTableCell("Values");
		doc.openTableCell("Logicial function");
		doc.openTableCell("Comment");
		
		for (Iterator it=graph.getNodeOrder().iterator() ; it.hasNext() ;) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			GsTreeInteractionsModel lfunc = vertex.getInteractionsModel();
			int nbval = 0;
			Object funcRoot = null;
			if (lfunc != null) {
				funcRoot = lfunc.getRoot();
				nbval = lfunc.getChildCount(funcRoot);
				if (nbval == 0) {
					funcRoot = null;
				}
			}
			doc.openTableRow();
			doc.openTableCell(1, nbval, vertex.getId()); //ID

			// the first logical function
			if (nbval > 0) {
				Object val = lfunc.getChild(funcRoot, 0);
				int nbfunc = lfunc.getChildCount(val);
				doc.openTableCell(val.toString()); //Values
				doc.openTableCell(""); //logical function
				doc.openList(DocumentWriter.LIST_STYLE_BULLET);
				for (int j=0 ; j<nbfunc ; j++) {
					Object func = lfunc.getChild(val, j);
					doc.openListItem(func.toString());
				}
				doc.closeList();
				doc.closeTableCell();
			} else {
				doc.openTableCell("");//Values (empty)
				doc.openTableCell("no function");//function
			}
			doc.openTableCell(1,nbval, "");
			writeAnnotation(vertex.getAnnotation());
			doc.closeTableRow();
			
			// add the other functions
			if (nbval > 1) {
				for (int i=1 ; i<nbval ; i++) {
					Object val = lfunc.getChild(funcRoot, i);
					int nbfunc = lfunc.getChildCount(val);
					doc.openTableRow();
					doc.openTableCell(val.toString());//values
					doc.openTableCell("");//function
					doc.openList(DocumentWriter.LIST_STYLE_BULLET);
					for (int j=0 ; j<nbfunc ; j++) {
						Object func = lfunc.getChild(val, j);
						doc.openListItem(func.toString());
					}
					doc.closeList();
					doc.closeTableCell();
					doc.closeTableRow();
				}
			} 
		}		
		doc.closeTable();		
	}
	
	public void writeAnnotation(Annotation annotation) throws IOException {
		boolean hasLink = false;
		Iterator it = annotation.getLinkList().iterator();
		if (it.hasNext()) {
			hasLink = true;
			doc.openList(DocumentWriter.LIST_STYLE_BULLET);
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
		//DocumentStyle styles = doc.getStyles();
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