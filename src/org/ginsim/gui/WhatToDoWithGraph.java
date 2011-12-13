package org.ginsim.gui;

import java.awt.EventQueue;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.servicegui.ServiceGUIManager;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.LayoutAction;
import org.ginsim.servicegui.common.ToolAction;

import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WhatToDoWithGraph extends JFrame {

	private static final int LIMIT_DISPLAY = 1000;
	
	private JPanel contentPane;

	private Graph graph;
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtn_applyLayoutAndOpen;
	private JRadioButton rdbtn_ApplyATool;
	private JRadioButton rdbtn_Save;
	private JRadioButton rdbtn_Export;
	private JComboBox comboBox_Layouts;
	private JComboBox comboBox_Tools;
	private JComboBox comboBox_Exports;
	
	private final List<LayoutAction> availableLayoutActions = new Vector<LayoutAction>();
	private final List<ToolAction> availableToolActions = new Vector<ToolAction>();
	private final List<ExportAction> availableExportActions = new Vector<ExportAction>();
	
	/**
	 * Launch the application.
	 */
	public static void main( String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WhatToDoWithGraph frame = new WhatToDoWithGraph( null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public WhatToDoWithGraph( Graph graph) {

		this.graph = graph;
		List<Action> available_actions = ServiceGUIManager.getManager().getAvailableActions( graph);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 425);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		this.setTitle( Translator.getString( "STR_whatToDo_title"));
		
		int graph_size = graph.getNodeCount();
		JLabel lbl_Alert = new JLabel( "<html>" + Translator.getString( "STR_whatToDo_alert1", "" + graph_size) + "</html>");
		if (graph_size >= LIMIT_DISPLAY){
			lbl_Alert.setText(  "<html>" + Translator.getString( "STR_whatToDo_alert2", "" + graph_size) + "</html>");
		}
		lbl_Alert.setBounds(56, 12, 368, 42);
		contentPane.add(lbl_Alert);
		
		JLabel lbl_MainQuestion = new JLabel( Translator.getString( "STR_whatToDo_question"));
		lbl_MainQuestion.setBounds(56, 66, 368, 15);
		contentPane.add(lbl_MainQuestion);
		
		rdbtn_applyLayoutAndOpen = new JRadioButton( Translator.getString( "STR_whatToDo_applyLayoutAndOpen"));
		buttonGroup.add(rdbtn_applyLayoutAndOpen);
		rdbtn_applyLayoutAndOpen.setBounds(80, 96, 344, 23);
		contentPane.add(rdbtn_applyLayoutAndOpen);
		
		comboBox_Layouts = new JComboBox();
		comboBox_Layouts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_applyLayoutAndOpen.setSelected( true);
			}
		});
		comboBox_Layouts.setBounds(127, 127, 231, 24);
		fillLayouts( comboBox_Layouts, available_actions);
		contentPane.add(comboBox_Layouts);
		if( graph_size >= LIMIT_DISPLAY){
			rdbtn_applyLayoutAndOpen.setEnabled( false);
			comboBox_Layouts.setEnabled( false);
		}
		
		rdbtn_ApplyATool = new JRadioButton(  Translator.getString( "STR_whatToDo_applyTool"));
		buttonGroup.add(rdbtn_ApplyATool);
		rdbtn_ApplyATool.setBounds(80, 170, 344, 23);
		contentPane.add(rdbtn_ApplyATool);
		
		comboBox_Tools = new JComboBox();
		comboBox_Tools.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_ApplyATool.setSelected( true);
			}
		});
		comboBox_Tools.setBounds(127, 201, 231, 24);
		fillTools( comboBox_Tools, available_actions);
		contentPane.add(comboBox_Tools);
		
		rdbtn_Save = new JRadioButton( Translator.getString( "STR_whatToDo_Save"));
		buttonGroup.add(rdbtn_Save);
		rdbtn_Save.setBounds(80, 317, 344, 23);
		contentPane.add(rdbtn_Save);
		
		rdbtn_Export = new JRadioButton( Translator.getString( "STR_whatToDo_Export"));
		buttonGroup.add(rdbtn_Export);
		rdbtn_Export.setBounds(80, 246, 344, 23);
		contentPane.add(rdbtn_Export);
		
		comboBox_Exports = new JComboBox();
		comboBox_Exports.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtn_Export.setSelected( true);
			}
		});
		fillExports( comboBox_Exports, available_actions);
		comboBox_Exports.setBounds(127, 277, 231, 24);
		contentPane.add(comboBox_Exports);
		
		JButton btn_Ok = new JButton( Translator.getString( "STR_OK"));
		btn_Ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean result = excecuteSelectedAction();
				if( result){
					closeWindow();
				}
			}
		});
		btn_Ok.setBounds(115, 362, 117, 25);
		contentPane.add(btn_Ok);
		
		JButton btn_Cancel = new JButton( Translator.getString( "STR_cancel"));
		btn_Cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		btn_Cancel.setBounds(244, 362, 117, 25);
		contentPane.add(btn_Cancel);
		
		if( graph_size < LIMIT_DISPLAY){
			rdbtn_applyLayoutAndOpen.setSelected( true);
		}
		else{
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
				if( current_button.equals( rdbtn_applyLayoutAndOpen)){
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
			ToolAction selected_tool = availableToolActions.get( selected_index);
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
	
	/**
	 * Close the frame
	 * 
	 */
	private void closeWindow(){
		
		this.setVisible( false);
		this.dispose();
	}
}
