package org.ginsim.servicegui.tool.stablestates;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.stablestate.StableStateSearcher;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.stablestates.StableStatesService;



/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends LogicalModelActionDialog  {

	private static StableStatesService sss = ServiceManager.getManager().getService(StableStatesService.class);
	
	StableTableModel model;
	JTable tresult;
	ObjectStore store = new ObjectStore();
	
	public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(lrg, f, "stableStatesGUI", 600, 400);
		
		model = new StableTableModel(lrg);
		tresult = new EnhancedJTable(model);
		
		// needed for the scroll bars to appear as needed
		tresult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tresult.getTableHeader().setReorderingAllowed(false);
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);
		
		Insets insets = new Insets(2, 2, 2, 2);
		JScrollPane pane = new JScrollPane(tresult);
		setMainPanel(pane);
	}
	
	@Override
	public void run(LogicalModel lmodel) {
		StableStateSearcher m_finder = sss.getStableStateSearcher(lmodel);
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
}



/**
 * custom cell renderer to colorize cells
 */
@SuppressWarnings("serial")
class ColoredCellRenderer extends DefaultTableCellRenderer {

	static final Color EVEN_BG = Color.WHITE, ODD_BG = new Color(220, 220, 220);
	static final Color STAR_BG = Color.CYAN, ACTIVE_BG = new Color(142, 142, 142);
	
	@Override
    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
                                                                                        int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
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
