package org.ginsim.servicegui.tool.css;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Translator;
import org.ginsim.commongui.dialog.SimpleDialog;
import org.ginsim.core.annotation.AnnotationLink;
import org.ginsim.core.annotation.BiblioList;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.view.css.CSSFile;
import org.ginsim.core.graph.view.css.CSSFilesAssociatedManager;
import org.ginsim.core.graph.view.css.CSSSyntaxException;
import org.ginsim.core.graph.view.css.CascadingStyleSheet;
import org.ginsim.core.graph.view.css.CascadingStyleSheetManager;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.core.utils.data.SimpleGenericList;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.ginsim.service.tool.reg2dyn.limitedsimulation.StatesToHierarchicalMappingManager;


public class CSSFrame extends SimpleDialog {

	private static final long serialVersionUID = 2012734705161939354L;
	private Graph graph;
	private JScrollPane cssListScrollPane, editorScrollPane;
	private SimpleGenericList cssList;
	private JTextArea editor;
	private JButton closeButton, saveButton, applyButton, restoreButton;
	private JPanel buttonsPanel;
	
	protected Colorizer colorizer;
	private GenericListPanel cssListPanel;
	
	private CSSFile currentEditedCSSFile = null;
		
	
	protected CascadingStyleSheetManager cssManager;
	
	public CSSFrame( Graph graph) {
		super(GUIManager.getInstance().getFrame(graph), "STR_cssFrame", 600, 400);
		this.graph = graph;
		cssManager = new CascadingStyleSheetManager(true);
        initialize();
        pack();
    }

	public void initialize() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
//		c.weightx = 1;
//		c.weighty = 1;
		this.add(getCSSList(), c);
		
		c.gridx++;
		this.add(getEditor(), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		this.add(getButtons(), c);
	}
	
	private Component getCSSList() {
		cssList = new CSSFilesList(graph);
		cssListPanel = new GenericListPanel();
		cssListPanel.setList(cssList);
		cssListPanel.addSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (currentEditedCSSFile != null){
					saveEditedCSSFile(currentEditedCSSFile);
				}
				if (lsm.isSelectionEmpty()) {
					editor.setEditable(false);
				} else {
					editor.setEditable(true);
					setEditedCSSFile();
				}
				
			}

		});
		
		return cssListPanel;
	}


	private Component getEditor() {
		editor = new JTextArea(20, 40);
		editorScrollPane = new JScrollPane(editor);
		editor.setEditable(false);

		return editorScrollPane;
	}

	private Component getButtons() {
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	
		c.gridx = 0;
		c.gridy = 0;
		saveButton = new JButton(Translator.getString("STR_save"));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();	
			}
		});
		buttonsPanel.add(saveButton, c);

		c.gridx++;
		applyButton = new JButton(Translator.getString("STR_css_apply"));
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				apply();	
			}
		});
		buttonsPanel.add(applyButton, c);

		c.gridx++;
		restoreButton = new JButton(Translator.getString("STR_css_restore"));
		restoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				restore();	
			}
		});
		buttonsPanel.add(restoreButton, c);

		c.gridx++;
		closeButton = new JButton(Translator.getString("STR_close"));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doClose();	
			}
		});
		buttonsPanel.add(closeButton, c);
		
		return buttonsPanel;
	}

	@Override
	public void doClose() {
		setVisible(false);
	}

	public void display() {
		setVisible(true);
	}
	
	
	private void setEditedCSSFile() {
		currentEditedCSSFile = (CSSFile)cssListPanel.getSelectedItem();
		editor.setText(currentEditedCSSFile.getTextToEdit());
	}

	private void saveEditedCSSFile(CSSFile editedCSSfile) {
		try {
			if (editedCSSfile != null) {
				editedCSSfile.saveEditedText(editor.getText());
				System.out.println("saveEditedCSSFile(): Saved edited text"+editor.getText());
			}
		} catch (CSSSyntaxException e) {
			NotificationManager.publishError(graph, e.getMessage());
		}
	}
	
	private void save() {
		saveEditedCSSFile(currentEditedCSSFile);
	}
	
	private void apply() {
		if (cssManager.shouldStoreOldStyle) {
			cssManager.storeAllEdges(graph.getEdges(), graph.getEdgeAttributeReader());
			cssManager.storeAllNodes(graph.getNodes(), graph.getNodeAttributeReader());
			cssManager.shouldStoreOldStyle = false;
		}
		CSSFile editedCSSfile = (CSSFile)cssListPanel.getSelectedItem();
		saveEditedCSSFile(currentEditedCSSFile);
		editedCSSfile.compileAndApply(graph, cssManager);
	}
	
	private void restore() {
		cssManager.restoreAllEdges(graph.getEdgeAttributeReader());
		cssManager.restoreAllNodes(graph.getNodeAttributeReader());
	}

	
}


class CSSFilesList extends SimpleGenericList {
	protected Graph graph;
	protected CSSFilesList( Graph graph) {
		super((List<CSSFile>)ObjectAssociationManager.getInstance().getObject(graph, CSSFilesAssociatedManager.KEY, true));
		this.graph = graph;

		
		canOrder = true;
		canAdd = true;
		canRemove = true;
		canEdit = true;
		doInlineAddRemove = false;
	}
	public Object doCreate(String name, int pos) {
		CSSFile cssFile = new CSSFile(name);
		return cssFile;
	}
	
	public boolean doEdit(Object data, Object value) {
		return ((CSSFile) data).setName((String)value);
	}
	
	
	public void doRun(int row, int col) {
		System.out.println("RUN @"+row+"&"+col);
	}
}

