package org.ginsim.servicegui.tool.stablestates;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.fixpoints.FixpointList;
import org.colomoto.biolqm.tool.fixpoints.FixpointTask;
import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.common.task.TaskStatus;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.gui.graph.dynamicgraph.StableTableModel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.stablestates.StableStatesService;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;



/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends LogicalModelActionDialog implements TaskListener, TableModelListener, ItemListener {

	private static StableStatesService sss = GSServiceManager.getService(StableStatesService.class);
	
	private StableTableModel model;
	private EnhancedJTable tresult;
	private FixpointTask m_finder;
	private JCheckBox cb_pattern, cb_extra;

    public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(lrg, f, "stableStatesGUI", 600, 400);
		setUserID("stable_search");
		this.setTitle(Txt.t("STR_stableStates_title"));

		model = new StableTableModel(lrg);
		model.addTableModelListener(this);
		tresult = new EnhancedJTable(model);
		tresult.setCopyHeaders();
		
		// needed for the scroll bars to appear as needed
		tresult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tresult.getTableHeader().setReorderingAllowed(false);
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);

		// auto-assign vertical headers to all but the first column
		// this works but the header becomes too narrow
//		new MixedTableHeader(tresult, 1);

		JScrollPane pane = new JScrollPane(tresult);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		cst.fill = GridBagConstraints.HORIZONTAL;
		cb_pattern = new JCheckBox("Pattern");
		panel.add(cb_pattern, cst);

		cst.gridx++;
		cb_extra = new JCheckBox("Extra components");
		panel.add(cb_extra, cst);
		cb_extra.addItemListener(this);

		cst.gridx = 0;
		cst.gridy++;
		cst.gridwidth = 2;
		cst.weightx = cst.weighty = 1;
		cst.fill = GridBagConstraints.BOTH;
		panel.add(pane, cst);
		setMainPanel(panel);
	}
	
	@Override
	public void run(LogicalModel lmodel) {
		m_finder = StableStatesService.getTask(lmodel);
		m_finder.pattern = cb_pattern.isSelected();
		m_finder.extra = cb_extra.isSelected();
		model.setResult(null);
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
		try {
			FixpointList result = m_finder.getResult();
			model.setResult(result);
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

    private void setTableHeader() {
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
            col.setMaxWidth(40);
            col.setPreferredWidth(30);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int t = e.getType();
        int r = e.getFirstRow();
        if (r == TableModelEvent.HEADER_ROW) {
            setTableHeader();
        }
    }

	@Override
	public void itemStateChanged(ItemEvent itemEvent) {
    	model.setExtra(cb_extra.isSelected());
	}

}
