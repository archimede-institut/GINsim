package org.ginsim.servicegui.tool.stablestates;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.common.task.TaskStatus;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.stablestate.StableStateSearcher;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.graph.dynamicgraph.StableTableModel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.stablestates.StableStatesService;



/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends LogicalModelActionDialog implements TaskListener {

	private static StableStatesService sss = ServiceManager.getManager().getService(StableStatesService.class);
	
	StableTableModel model;
	JTable tresult;

    StableStateSearcher m_finder;

    public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(lrg, f, "stableStatesGUI", 600, 400);
		setUserID("stable_search");
		
		model = new StableTableModel(lrg);
		tresult = new EnhancedJTable(model);
		
		// needed for the scroll bars to appear as needed
		tresult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tresult.getTableHeader().setReorderingAllowed(false);
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);
		
		JScrollPane pane = new JScrollPane(tresult);
		setMainPanel(pane);
	}
	
	@Override
	public void run(LogicalModel lmodel) {
		m_finder = sss.getStableStateSearcher(lmodel);
        setRunning(true);
        m_finder.background(this);
    }

    public void taskUpdated(Task task) {
        if (task != m_finder) {
            return;
        }

        TaskStatus status = m_finder.getStatus();
        if (status == TaskStatus.CANCELED) {
            setRunning(false);
            cancel();
            return;
        }

        setRunning(false);
        StableStateSearcher m_finder = (StableStateSearcher)task;
		try {
			int result = m_finder.getResult();
			model.setResult(m_finder.getMDDManager(), result);
			m_finder.getMDDManager().free(result);
	
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
			
		} catch (Exception e) {
			LogManager.error(e);
		}
	}

    @Override
    protected boolean doCancel() {
        if (m_finder != null && m_finder.getStatus() == TaskStatus.RUNNING) {
            m_finder.cancel();
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
