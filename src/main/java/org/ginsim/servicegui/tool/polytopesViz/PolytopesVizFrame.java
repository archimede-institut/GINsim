package org.ginsim.servicegui.tool.polytopesViz;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.ParseException;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.view.style.ColorizerPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.polytopesViz.PolytopesService;


public class PolytopesVizFrame extends StackDialog {
	private static final long serialVersionUID = -7619253564236142617L;
	private DynamicGraph graph;
	private Container mainPanel;
	private ColorizerPanel colPanel;
	private JTextField polytopeTextField;
	
	
	public PolytopesVizFrame( DynamicGraph graph) {
		super(GUIManager.getInstance().getFrame(graph), "STR_polytopesViz", 475, 260);
		this.graph = graph;
        initialize();
    }

	public void initialize() {
		setMainPanel(getMainPanel());
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			polytopeTextField = new JTextField(25);
			polytopeTextField.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					try {
						ServiceManager.getManager().getService(PolytopesService.class).run(graph, polytopeTextField.getText());
					} catch (ParseException msg) {
						LogManager.error(msg);
					}
				}
			});
			
			mainPanel.add(polytopeTextField, c);
			
			c.gridy++;
			c.weightx = 0;
			colPanel = new ColorizerPanel(graph);
            // FIXME: use a StyleProvider here
//			colPanel.setNewColorizer(ServiceManager.getManager().getService(PolytopesService.class).getColorizer());
			mainPanel.add(colPanel, c);
		}
		return mainPanel;
	}


	protected void run() {
		try {
			ServiceManager.getManager().getService(PolytopesService.class).run(graph, polytopeTextField.getText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		colPanel.doColorize();
	}
	
	
}
