package org.ginsim.servicegui.tool.stablestates;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.fixpoints.FixpointList;
import org.colomoto.biolqm.tool.fixpoints.FixpointTask;
import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.common.task.TaskStatus;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.commongui.utils.MixedTableHeader;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.gui.graph.dynamicgraph.StableTableModel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.stablestates.StableStatesService;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.Enumeration;



/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends LogicalModelActionDialog implements TaskListener {

	private static StableStatesService sss = GSServiceManager.getService(StableStatesService.class);
	
	private StableTableModel model;
	private EnhancedJTable tresult;
	private FixpointTask m_finder;

    public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(lrg, f, "stableStatesGUI", 600, 400);
		setUserID("stable_search");
		this.setTitle(Txt.t("STR_stableStates_title"));

		model = new StableTableModel(lrg);
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
		setMainPanel(pane);
	}
	
	@Override
	public void run(LogicalModel lmodel) {
		m_finder = StableStatesService.getTask(lmodel);
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
