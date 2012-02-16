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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.ginsim.commongui.utils.VerticalTableHeaderCellRenderer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.service.tool.stablestates.StableStateFinder;

import fr.univmrs.tagc.javaMDD.MDDFactory;
import fr.univmrs.tagc.javaMDD.MultiValuedVariable;
import fr.univmrs.tagc.javaMDD.PathSearcher;


/**
 * A simple GUI to launch stable states search and view the result
 * 
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class StableStateSwingUI extends StackDialog  {

	RegulatoryGraph m_lrg;
	StableStateFinder m_finder;
	NewStableTableModel model;
	JTable tresult;
	
	public StableStateSwingUI(JFrame f, RegulatoryGraph lrg) {
		super(f, "stableStatesGUI", 600, 400);
		this.m_lrg = lrg;
		this.m_finder = new StableStateFinder(lrg);
		
		model = new NewStableTableModel();
		tresult = new JTable(model);
		
		tresult.setDefaultRenderer(Object.class, new ColoredCellRenderer());
		tresult.setAutoCreateRowSorter(true);
		
		JPanel panel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(2, 2, 2, 2);
		GridBagConstraints cst = new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				insets, 0, 0);
		panel.add(new JLabel("TODO: mutant and input selector"), cst);
		JScrollPane pane = new JScrollPane();
	    pane.setViewportView(tresult);
	    cst = new GridBagConstraints(0, 1, 1, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				insets, 0, 0);
	    panel.add(pane, cst);
		setMainPanel(panel);
	}
	
	@Override
	protected void run() {
		int result = m_finder.find();
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
 * Simple table model to view stable state search results.
 */
@SuppressWarnings("serial")
class NewStableTableModel extends AbstractTableModel {

	int nbcol = 0;
	List<int[]> result = new ArrayList<int[]>();
	MultiValuedVariable[] variables;

	public int[] getState(int sel) {
		return result.get(sel);
	}

	public void setResult(MDDFactory factory, int idx) {
		result.clear();
		variables = factory.getVariables();
		nbcol = variables.length;
		if (!factory.isleaf(idx)) {
			PathSearcher searcher = new PathSearcher(1);
			int[] path = searcher.setNode(factory, idx);
			for (int l: searcher) {
				result.add(path.clone());
			}
		}
		
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		return result.size();
	}

	@Override
	public int getColumnCount() {
		return nbcol;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int v = result.get(rowIndex)[columnIndex];
		if (v == 0) {
			return "";
		}
		if (v < 0) {
			return "*";
		}
		return ""+v;
	}

	@Override
	public String getColumnName(int column) {
		if (variables != null && variables[column] != null) {
			return variables[column].name;
		}
		return super.getColumnName(column);
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
