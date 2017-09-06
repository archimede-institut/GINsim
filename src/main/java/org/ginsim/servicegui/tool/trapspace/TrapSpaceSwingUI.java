package org.ginsim.servicegui.tool.trapspace;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.helper.clingo.ClingoLauncher;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceList;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceSettings;
import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.common.task.TaskStatus;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.trapspacetree.TrapSpaceTree;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.trapspace.TrapSpaceService;



/**
 * A simple GUI to launch the identification of trap-spaces and view the result.
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class TrapSpaceSwingUI extends LogicalModelActionDialog implements TaskListener {

	private static TrapSpaceService srv = GSServiceManager.getService(TrapSpaceService.class);
	
	TrapSpaceTableModel model;
	JTable tresult;
	JCheckBox cb_tree;
	
	private final TrapSpaceSettings settings;
	Task<TrapSpaceList> m_identifier;

    public TrapSpaceSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(lrg, f, "stableStatesGUI", 600, 400);
		setUserID("stable_search");
		
		model = new TrapSpaceTableModel();
		tresult = new EnhancedJTable(model);
		settings = srv.getSettings();
		settings.terminal = false;
		settings.tree = true;
		settings.bdd = !ClingoLauncher.isAvailable();
		settings.reduce = false;
		
		// needed for the scroll bars to appear as needed
		tresult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tresult.getTableHeader().setReorderingAllowed(false);
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);
		
		JPanel main = new JPanel(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		cb_tree = new JCheckBox("Generate inclusion tree");
		main.add(cb_tree, cst);
		cst.gridy++;
		cst.weightx = 1;
		cst.weighty = 1;
		cst.fill = GridBagConstraints.BOTH;
		main.add(new JScrollPane(tresult), cst);
		setMainPanel(main);
	}
	
	@Override
	public void run(LogicalModel lmodel) {
        setRunning(true);
        if (cb_tree.isSelected()) {
        	settings.tree = true;
        	settings.terminal = false;
        } else {
        	settings.tree = false;
        	settings.terminal = true;
        }
		m_identifier = srv.getTask(lmodel, settings);
		m_identifier.background(this);
    }

    public void taskUpdated(Task task) {
        if (task != m_identifier) {
            return;
        }

        TaskStatus status = m_identifier.getStatus();
        if (status == TaskStatus.CANCELED) {
            setRunning(false);
            cancel();
            return;
        }

        setRunning(false);
		TrapSpaceList solutions = m_identifier.getResult();
		if (solutions == null) {
			System.out.println("No solution");
			return;
		}
		
		if (settings.tree) {
			TrapSpaceTree tree = GSGraphManager.getInstance().getNewGraph(TrapSpaceTree.class, solutions);
			tree.setAssociatedGraph(lrg);
			GUIManager.getInstance().whatToDoWithGraph( tree);
			cancel();
			return;
		}
		
		model.setSolutions(solutions);

		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		Enumeration<TableColumn> columns = tresult.getColumnModel().getColumns();
		
		if (columns.hasMoreElements()) {
			TableColumn col = columns.nextElement();
			col.setMinWidth(150);
			col.setMaxWidth(400);
		}
		while (columns.hasMoreElements()) {
			TableColumn col = columns.nextElement();
			col.setHeaderRenderer(headerRenderer);
			col.setMinWidth(20);
			col.setMaxWidth(25);
		}
	}

    @Override
    protected boolean doCancel() {
        if (m_identifier != null && m_identifier.getStatus() == TaskStatus.RUNNING) {
            m_identifier.cancel();
            return false;
        }
        return true;
    }
}



/**
 * custom cell renderer to colorize cells
 */
@SuppressWarnings("serial")
class ColoredCellRenderer extends DefaultTableCellRenderer {

	static final Color EVEN_BG = new Color(255, 255, 200), ODD_BG = new Color(220, 220, 150);
	static final Color STAR_BG = Color.CYAN, ACTIVE_BG = new Color(142, 142, 142);
	
	@Override
    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
                                                                                        int row , int column ) {
        Component cmp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if( table != null && row >= 0) {
            if (column == 0 || "0".equals(value)) {
            	if (isSelected) {
            		cmp.setBackground(table.getSelectionBackground());
            	} else {
            		cmp.setBackground(row%2 == 0 ? EVEN_BG : ODD_BG);
            	}
            	if (column == 0) {
            		cmp.setForeground(Color.BLACK);
            	} else {
            		cmp.setForeground(cmp.getBackground());
            	}
        	} else if ("*".equals(value)) {
                cmp.setBackground(STAR_BG);
        		cmp.setForeground(Color.BLACK);
            } else {
        		cmp.setForeground(Color.BLACK);
            	cmp.setBackground(ACTIVE_BG);
            }
        }
        return cmp;
    }
}
