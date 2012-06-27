package org.ginsim.servicegui.tool.stablestates;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.stablestates.StableStateFinder;



/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends StackDialog  {

	RegulatoryGraph m_lrg;
	StableStateFinder m_finder;
	StableTableModel model;
	JTable tresult;
	ObjectStore store = new ObjectStore();
	
	public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(f, "stableStatesGUI", 600, 400);
		this.m_lrg = lrg;
		this.m_finder = new StableStateFinder(lrg);
		
		model = new StableTableModel();
		tresult = new EnhancedJTable(model);
		
		// needed for the scroll bars to appear as needed
		tresult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tresult.getTableHeader().setReorderingAllowed(false);
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);
		
		JPanel panel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(2, 2, 2, 2);
		GridBagConstraints cst = new GridBagConstraints(0, 0, 1, 1, 1, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				insets, 0, 0);
		MutantSelectionPanel  mutantPanel = new MutantSelectionPanel(this, lrg, store);
		panel.add(mutantPanel, cst);
		JScrollPane pane = new JScrollPane(tresult);
	    cst = new GridBagConstraints(0, 1, 1, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				insets, 0, 0);
	    panel.add(pane, cst);
		setMainPanel(panel);
	}
	
	@Override
	protected void run() {
		Perturbation perturbation = (Perturbation)store.getObject(0);
		m_finder.setPerturbation(perturbation);
		int result = m_finder.call();
		model.setResult(m_finder.getFactory(), result);
		m_finder.getFactory().free(result);

		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		Enumeration<TableColumn> columns = tresult.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
		   TableColumn col = columns.nextElement();
		   col.setHeaderRenderer(headerRenderer);
		   col.setMinWidth(20);
		   col.setMaxWidth(25);
		}
	}
	
	@Override
    protected void cancel() {
		// TODO: ???
		super.cancel();
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
            if ("*".equals(value)) {
                cmp.setBackground(STAR_BG);
            } else if ("".equals(value)) {
            	if (isSelected) {
            		cmp.setBackground(table.getSelectionBackground());
            	} else {
            		cmp.setBackground(row%2 == 0 ? EVEN_BG : ODD_BG);
            	}
            } else {
            	cmp.setBackground(ACTIVE_BG);
            }
        }
        return cmp;
    }
}
