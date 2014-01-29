package org.ginsim.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Translator;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.GraphGUIHelperFactory;
import org.ginsim.gui.service.ServiceGUIManager;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.gui.shell.actions.LayoutAction;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;

/**
 * This class define a frame that is opened when a graph is computed from an other one.
 * This frame propose to do one of the following actions:
 *  - Layout and display the graph
 *  - Apply a tool to the graph (one of the entry of the Tool menu)
 *  - Export the graph to a specified file format
 *  - Save the graph to a file (zginml)
 *  
 *  If the graph size exceed a given number (LIMIT_WARNING), a warning message explain that displaying the graph
 *  may be very resource consuming
 *  If the graph size exceed an other given number (LIMIT_DISABLE_DISPLAY), a warning message explain that the display
 *  of the graph is disabled because the graph is too large
 * 
 * 
 * @author Lionel Spinelli
 *
 */
public class WhatToDoWithGraph extends Frame {

	public static final int LITMIT_ASK_QUESTION = 50;
	private static final int LIMIT_WARNING = 500;
	private static final int LIMIT_DISABLE_DISPLAY = 1000;
	
	private JPanel contentPane;

	private Graph graph;
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtn_view;
	private JRadioButton rdbtn_applyLayoutAndOpen;
	private JRadioButton rdbtn_ApplyATool;
	private JRadioButton rdbtn_Save;
	private JRadioButton rdbtn_Export;
	private JComboBox comboBox_Layouts;
	private JComboBox comboBox_Tools;
	private JComboBox comboBox_Exports;
	
	private final List<LayoutAction> availableLayoutActions = new Vector<LayoutAction>();
	private final List<Action> availableToolActions = new Vector<Action>();
	private final List<ExportAction> availableExportActions = new Vector<ExportAction>();
	
	/**
	 * Create the frame.
	 */
	public WhatToDoWithGraph( Graph graph) {
		super("whattodo", 400, 450);
		this.graph = graph;
		// Retrieve the list of available actions for the graph type
		List<Action> available_actions = ServiceGUIManager.getManager().getAvailableActions( graph);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		this.setTitle( Translator.getString( "STR_whatToDo_title"));
		
		// Display the right message according to the graph size
		int graph_size = graph.getNodeCount();
		JLabel lbl_Alert = new JLabel( "<html>" + Translator.getString( "STR_whatToDo_message", "" + graph_size) + "</html>");
		if( graph_size >= LIMIT_WARNING && graph_size < LIMIT_DISABLE_DISPLAY){
			lbl_Alert.setText(  "<html>" + Translator.getString( "STR_whatToDo_alert1", "" + graph_size) + "</html>");
		}
		else if (graph_size >= LIMIT_DISABLE_DISPLAY){
			lbl_Alert.setText(  "<html>" + Translator.getString( "STR_whatToDo_alert2", "" + graph_size) + "</html>");
		}
		lbl_Alert.setBounds(56, 12, 368, 42);
		contentPane.add(lbl_Alert);
		
		JLabel lbl_MainQuestion = new JLabel( Translator.getString( "STR_whatToDo_question"));
		lbl_MainQuestion.setBounds(56, 66, 368, 15);
		contentPane.add(lbl_MainQuestion);

		// add graph info panel
		try {
			GraphGUIHelper guiHelper = GraphGUIHelperFactory.getFactory().getGraphGUIHelper(graph);
			Component panel = guiHelper.getInfoPanel(graph);
			if (panel != null) {
				panel.setBounds(450, 10, 200, 440);
				contentPane.add(panel);
				Rectangle bounds = getBounds();
				bounds.width += 220;
				setBounds(bounds);
			}
			
		} catch (Exception e) {}


		// Build the Layout radio button and the Layout combo box
		rdbtn_view = new JRadioButton( Translator.getString( "STR_whatToDo_view"));
		buttonGroup.add(rdbtn_view);
		rdbtn_view.setBounds(80, 95, 344, 23);
		contentPane.add(rdbtn_view);
		
		rdbtn_applyLayoutAndOpen = new JRadioButton( Translator.getString( "STR_whatToDo_applyLayoutAndOpen"));
		buttonGroup.add(rdbtn_applyLayoutAndOpen);
		rdbtn_applyLayoutAndOpen.setBounds(80, 127, 344, 23);
		contentPane.add(rdbtn_applyLayoutAndOpen);
		
		comboBox_Layouts = new JComboBox();
		comboBox_Layouts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_applyLayoutAndOpen.setSelected( true);
			}
		});
		comboBox_Layouts.setBounds(127, 150, 231, 24);
		fillLayouts( comboBox_Layouts, available_actions);
		contentPane.add(comboBox_Layouts);
		// If display limit is reached, display option is grayed out
		if( graph_size >= LIMIT_DISABLE_DISPLAY){
			rdbtn_applyLayoutAndOpen.setEnabled( false);
			rdbtn_view.setEnabled( false);
			comboBox_Layouts.setEnabled( false);
		}

		// Build the Tool radio button and the Tool combo box
		rdbtn_ApplyATool = new JRadioButton(  Translator.getString( "STR_whatToDo_applyTool"));
		buttonGroup.add(rdbtn_ApplyATool);
		rdbtn_ApplyATool.setBounds(80, 190, 344, 23);
		contentPane.add(rdbtn_ApplyATool);
		
		comboBox_Tools = new JComboBox();
		comboBox_Tools.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_ApplyATool.setSelected( true);
			}
		});
		comboBox_Tools.setBounds(127, 215, 231, 24);
		fillTools( comboBox_Tools, available_actions);
		contentPane.add(comboBox_Tools);
		
		// Build the Export radio button and the Export combo box
		rdbtn_Export = new JRadioButton( Translator.getString( "STR_whatToDo_Export"));
		buttonGroup.add(rdbtn_Export);
		rdbtn_Export.setBounds(80, 260, 344, 23);
		contentPane.add(rdbtn_Export);
		
		comboBox_Exports = new JComboBox();
		comboBox_Exports.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_Export.setSelected( true);
			}
		});
		fillExports( comboBox_Exports, available_actions);
		comboBox_Exports.setBounds(127, 297, 231, 24);
		contentPane.add(comboBox_Exports);
		
		// Build the Save radio button
		rdbtn_Save = new JRadioButton( Translator.getString( "STR_whatToDo_Save"));
		buttonGroup.add(rdbtn_Save);
		rdbtn_Save.setBounds(80, 337, 344, 23);
		contentPane.add(rdbtn_Save);
		
		// Build the OK button
		JButton btn_Ok = new JButton( Translator.getString( "STR_OK"));
		btn_Ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean result = excecuteSelectedAction();
				if( result){
					close();
				}
			}
		});
		btn_Ok.setBounds(115, 380, 117, 25);
		contentPane.add(btn_Ok);
		
		//Build the Cancel button 
		JButton btn_Cancel = new JButton( Translator.getString( "STR_cancel"));
		btn_Cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				close();
			}
		});
		btn_Cancel.setBounds(244, 380, 117, 25);
		contentPane.add(btn_Cancel);
		
		// If display limit is reached, default selected option is "apply tool" instead of "display"
		if( graph_size < LIMIT_DISABLE_DISPLAY){
			// guess if a layout is needed
			if (needLayout(graph)) {
				rdbtn_applyLayoutAndOpen.setSelected( true);
			} else {
				rdbtn_view.setSelected( true);
			}
		} else{
			rdbtn_ApplyATool.setSelected( true);
		}
		this.setVisible( true);
	}
	
	/**
	 * Fill the choose Layout combo box with the list of available layouts
	 * 
	 * @param comboBox_Layouts the combo box to fill
	 * @param available_actions the list of available actions
	 */
	private void fillLayouts( JComboBox comboBox_Layouts, List<Action> available_actions){
		
		for (Action action: available_actions) {
			if (action instanceof LayoutAction) {
				availableLayoutActions.add( (LayoutAction) action);
				comboBox_Layouts.addItem( Translator.getString( action.getValue( Action.NAME).toString()));
			}
		}
		comboBox_Layouts.addItem( Translator.getString( "STR_whattodo_NoLayout"));
	}
	
	/**
	 * Fill the choose Tools combo box with the list of available actions
	 * 
	 * @param comboBox_Tools the combo box to fill
	 * @param available_actions the list of available actions
	 */
	private void fillTools( JComboBox comboBox_Tools, List<Action> available_actions){
		
		for (Action action: available_actions) {
			if (action instanceof ToolAction) {
				availableToolActions.add( (ToolAction) action);
				comboBox_Tools.addItem( Translator.getString( action.getValue( Action.NAME).toString()));
			} else if (action instanceof GenericGraphAction) {
				availableToolActions.add( (GenericGraphAction) action);
				comboBox_Tools.addItem( Translator.getString( action.getValue( Action.NAME).toString()));
			}
		}
	}
	
	/**
	 * Fill the choose Export combo box with the list of available actions
	 * 
	 * @param comboBox_Exports the combo box to fill
	 * @param available_actions the list of available actions
	 */
	private void fillExports( JComboBox comboBox_Exports, List<Action> available_actions){
		
		for (Action action: available_actions) {
			if (action instanceof ExportAction) {
				availableExportActions.add( (ExportAction) action);
				comboBox_Exports.addItem( Translator.getString( action.getValue( Action.NAME).toString()));
			}
		}
	}
	
	/**
	 * 
	 */
	private boolean excecuteSelectedAction(){
		
		Enumeration<AbstractButton> button_enum = buttonGroup.getElements();
		while( button_enum.hasMoreElements()){
			AbstractButton current_button = button_enum.nextElement();
			if( current_button.isSelected()){
				if( current_button.equals( rdbtn_view)){
					GUIManager.getInstance().newFrame( graph);
					return true;
				}
				else if( current_button.equals( rdbtn_applyLayoutAndOpen)){
					boolean result = executeSelectedLayout();
					if( !result){
						GUIMessageUtils.openErrorDialog( "STR_whatToDo_selectedLayoutNotCorrect");
					}
					return result;
				}
				else if( current_button.equals( rdbtn_ApplyATool)){
					boolean result = executeSelectedTool();
					if( !result){
						GUIMessageUtils.openErrorDialog( "STR_whatToDo_selectedToolNotCorrect");
					}
					return result;
				}
				else if( current_button.equals( rdbtn_Export)){
					boolean result = executeSelectedExport();
					if( !result){
						GUIMessageUtils.openErrorDialog( "STR_whatToDo_selectedExportNotCorrect");
					}
					return result;
				}
				else if( current_button.equals( rdbtn_Save)){
					return executeSave();
				}
			}
		}
		return false;
	}
	
	/**
	 * Execute the Layout action selected in the combobox
	 * 
	 * false if the selected action is not a pre-defined action
	 */
	private boolean executeSelectedLayout(){
		
		int selected_index = comboBox_Layouts.getSelectedIndex();
		if( selected_index >= 0 && selected_index < availableLayoutActions.size()){
			LayoutAction selected_layout = availableLayoutActions.get( selected_index);
			selected_layout.actionPerformed( null);
		}
		if( selected_index < 0 || selected_index > availableLayoutActions.size()){
			return false;
		}
		GUIManager.getInstance().newFrame( graph);
		return true;
	}
	
	/**
	 * Execute the Tool action selected in the combobox
	 * 
	 * false if the selected action is not a pre-defined action
	 */
	private boolean executeSelectedTool(){
		
		int selected_index = comboBox_Tools.getSelectedIndex();
		if( selected_index >= 0 && selected_index < availableToolActions.size()){
			Action selected_tool = availableToolActions.get( selected_index);
			selected_tool.actionPerformed( null);
			return true;
		}
		return false;
	}
	
	/**
	 * Execute the Export action selected in the combobox
	 * 
	 * @return false if the selected action is not a pre-defined action
	 */
	private boolean executeSelectedExport(){
		
		int selected_index = comboBox_Exports.getSelectedIndex();
		if( selected_index >= 0 && selected_index < availableExportActions.size()){
			ExportAction selected_export = availableExportActions.get( selected_index);
			selected_export.actionPerformed( null);
			return true;
		}
		return false;
	}
	
	/**
	 * Save the graph to file
	 * 
	 * @return true if save was correctly done 
	 */
	private boolean executeSave(){
		
		String savePath = GraphManager.getInstance().getGraphPath( graph);
		if (savePath == null) {
			GsFileFilter ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] { "zginml" }, "GINsim files");
			savePath = FileSelectionHelper.selectSaveFilename( this, ffilter);
			if (savePath != null) {
				try {
					graph.save( savePath);
					return true;
				} catch (Exception e) {
					GUIMessageUtils.openErrorDialog( "Unable to save file. See logs for more details");
					LogManager.error( "Unable to save file : " + savePath);
					LogManager.error( e);
				}
			}
		}
		return false;
	}

	@Override
	public void close(){
		
		this.setVisible( false);
		this.dispose();
	}
	
	private static boolean needLayout(Graph graph) {
		NodeAttributesReader nreader = graph.getNodeAttributeReader();
		int n = 0;
		for (Object o: graph.getNodes()) {
			nreader.setNode(o);
			int x = nreader.getX();
			int y = nreader.getY();
			if (x != 0 || y != 0) {
				return false;
			}
			
			if (n > 100) {
				break;
			}
			n++;
		}

		return true;
	}

	public static void layoutIfNeeded(Graph<?, ?> graph) {
		if (needLayout(graph)) {
			for (Action action: ServiceGUIManager.getManager().getAvailableActions( graph)) {
				if (action instanceof LayoutAction) {
					action.actionPerformed(null);
					break;
				}
			}
		}
		
	}

}
