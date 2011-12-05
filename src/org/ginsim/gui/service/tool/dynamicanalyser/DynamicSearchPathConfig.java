package org.ginsim.gui.service.tool.dynamicanalyser;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.service.tool.regulatorygraphanimation.AReg2GPConfig;
import org.ginsim.gui.utils.widgets.EnhancedJTable;


/**
 * UI to search a path in the dynamic graph.
 */
public class DynamicSearchPathConfig extends JDialog {

    private static final long serialVersionUID = -1664002987035655174L;
    
    private DynamicGraph graph;
    private List v_pathConstraints = new ArrayList(2);
    
    private JPanel jcontentPane = null;
    private JScrollPane scrollPane = null;
    private JTable tableConstraint = null;
    private JButton but_search = null;
    private JButton but_add = null;
    private JButton but_del = null;
    private JButton but_gp = null;
    private JButton but_view = null;
    private JButton but_close = null;
    private JLabel l_info = null;
    private JPanel p_result = null;
    
    private DynamicAnalyserPathModel model;
    private List path = null;
    private JFrame frame;
    private String s_nodeOrder;
    
    /**
     * get ready 
     * @param main
     * 
     * @param graph
     */
    public DynamicSearchPathConfig(JFrame main, DynamicGraph graph) {
        super(main);
        this.frame = main;
        this.graph = graph;
        List nodeOrder = graph.getNodeOrder();
        s_nodeOrder = "";
        for (int i=0 ; i<nodeOrder.size() ; i++) {
            s_nodeOrder += nodeOrder.get(i)+" ";
        }
        initialize();
        
        Object o = ObjectAssociationManager.getInstance().getObject( graph, "reg2dyn_firstState", false);
        if (o != null && o instanceof int[]) {
        	byte[] t1 = (byte[])o;
        	byte[] t2 = new byte[t1.length];
            for (int i=0 ; i<t1.length ; i++) {
                t2[i] = t1[i];
            }
            v_pathConstraints.clear();
            v_pathConstraints.add(t1);
            v_pathConstraints.add(t2);
        }
    }
    
    private void initialize() {
        setSize(400, 230);
        setContentPane(getJContentPane());
        setVisible(true);
        
        changed(model.isOk());
    }
    
    /**
     * @return the content pane
     */
    private Container getJContentPane() {
        if (jcontentPane == null) {
            jcontentPane = new JPanel();
            jcontentPane.setLayout(new GridBagLayout());
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 5;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            jcontentPane.add(getScrollPane(), c);
            
            c = new GridBagConstraints();
            c.gridx = 4;
            c.gridy = 3;
            c.weightx = 0;
            jcontentPane.add(getBut_search(), c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0;
            jcontentPane.add(getBut_add(), c);
            
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 0;
            jcontentPane.add(getBut_del(), c);
            
            c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = 3;
            c.anchor = GridBagConstraints.EAST;
            c.weightx = 1;
            jcontentPane.add(getBut_close(), c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 5;
            c.weightx = 1;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(10, 0, 10, 0);
            jcontentPane.add(getP_result(), c);
        }
        return jcontentPane;
    }

    private JPanel getP_result() {
    	if (p_result == null) {
    		p_result = new JPanel();
    		p_result.setLayout(new GridBagLayout());
    		GridBagConstraints c = new GridBagConstraints();
    		c.gridx = 0;
    		c.gridy = 0;
    		c.weightx = 1;
    		c.fill = GridBagConstraints.BOTH;
    		p_result.add(getL_info(), c);

    		c = new GridBagConstraints();
    		c.gridx = 1;
    		c.gridy = 0;
    		c.anchor = GridBagConstraints.WEST;
    		p_result.add(getBut_gp(), c);

    		c = new GridBagConstraints();
    		c.gridx = 2;
    		c.gridy = 0;
    		p_result.add(getBut_view(), c);
    	}
    	return p_result;
    }
    
    /**
     * @return l_info
     */
    private JLabel getL_info() {
        if (l_info == null) {
            l_info = new JLabel();
        }
        return l_info;
    }

    protected void search() {
        boolean isPath = false;
        if (path == null) {
            path = new ArrayList();
        } else {
            path.clear();
        }
        for (int i=1 ; i<v_pathConstraints.size() ; i++) {
            List<Edge> l = graph.shortestPath((byte[])v_pathConstraints.get(i-1), (byte[])v_pathConstraints.get(i));
            isPath = true;
            if (l == null) {
                isPath = false;
                break;
            }
            if (i == 1 && i<v_pathConstraints.size()) {
                path.add(l.get(0).getSource());
            }
            for (int j=0 ; j<l.size() ; j++) {
            	Edge edge = l.get(j);
                path.add(edge.getTarget());
            }
        }
        if (isPath) {
            but_gp.setEnabled(true);
            but_view.setEnabled(true);
            l_info.setText("path found ["+path.size()+"]");
        } else {
            path.clear();
            but_gp.setEnabled(false);
            but_view.setEnabled(false);
            l_info.setText("path not found");
        }
    }
    
    protected void add() {
        int index = tableConstraint.getSelectedRow();
        int c = tableConstraint.getSelectedColumn();
        model.add(index);
        if (index > -1 && index < tableConstraint.getRowCount()) {
            tableConstraint.setRowSelectionInterval(index,index);
            tableConstraint.setColumnSelectionInterval(c,c);
        }
        but_del.setEnabled(true);
    }
    
    protected void del() {
        int index = tableConstraint.getSelectedRow();
        int c = tableConstraint.getSelectedColumn();
        model.del(index--);
        if (index > -1 && index < tableConstraint.getRowCount()) {
            tableConstraint.setRowSelectionInterval(index,index);
            tableConstraint.setColumnSelectionInterval(c,c);
        }
        if (model.getRowCount() < 3) {
            but_del.setEnabled(false);
        }
    }

    protected void close() {
        setVisible(false);
    }
    
    protected void view() {
        if (this.path == null) {
            return;
        }
        Vector v_path = new Vector(this.path.size()+1);
        v_path.add(s_nodeOrder);
        v_path.addAll(this.path);
        JDialog pathViewDialog = new JDialog(frame);
        int w = 10*s_nodeOrder.length();
        w = w > 800 ? 800 : w;
        int h = 40+25*path.size();
        h = h>600 ? 600 : h;
        pathViewDialog.setSize(w,h);
        JScrollPane scrollPane = new JScrollPane();
        pathViewDialog.setContentPane(scrollPane);
        scrollPane.setViewportView(new JList(v_path));
        pathViewDialog.setVisible(true);
    }
    
    protected void gnuplotExport() {
        if (path != null && path.size() != 0) {
            new AReg2GPConfig(frame, path, graph.getNodeOrder());
        }
    }
    
    private JButton getBut_search() {
        if (but_search == null) {
            but_search = new JButton(Translator.getString("STR_run"));
            but_search.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    search();
                }
            });
        }
        return but_search;
    }
    private JButton getBut_add() {
        if (but_add == null) {
            but_add = new JButton("+");
            but_add.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    add();
                }
            });
        }
        return but_add;
    }
    private JButton getBut_del() {
        if (but_del == null) {
            but_del = new JButton("X");
            but_del.setForeground(Color.RED);
            but_del.setEnabled(false);
            but_del.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    del();
                }
            });
        }
        return but_del;
    }
    private JButton getBut_gp() {
        if (but_gp == null) {
            but_gp = new JButton("gnuplot");
            but_gp.setToolTipText(Translator.getString("STR_gnuplot_descr"));
            but_gp.setEnabled(false);
            but_gp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gnuplotExport();
                }
            });
        }
        return but_gp;
    }
    private JButton getBut_view() {
        if (but_view == null) {
            but_view = new JButton(Translator.getString("STR_View"));
            but_view.setEnabled(false);
            but_view.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    view();
                }
            });
        }
        return but_view;
    }
    private JButton getBut_close() {
        if (but_close == null) {
            but_close = new JButton(Translator.getString("STR_close"));
            but_close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
        }
        return but_close;
    }
    private JTable getTableConstraint() {
        if (tableConstraint == null) {
            tableConstraint = new EnhancedJTable();
            List v_inPath = new ArrayList(2);
            model = new DynamicAnalyserPathModel(graph, v_pathConstraints, v_inPath, this);
            tableConstraint.setModel(model);
            tableConstraint.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tableConstraint.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            tableConstraint.getTableHeader().setReorderingAllowed(false);
            tableConstraint.setDefaultRenderer(Object.class, new DynamicPathItemCellRenderer(v_inPath));

            Enumeration e_col = tableConstraint.getColumnModel().getColumns();
            int i=-1;
            while (e_col.hasMoreElements()) {
                TableColumn col = (TableColumn)e_col.nextElement();
                i++;
                int w = 10+8*graph.getNodeOrder().get(i).toString().length();
                col.setPreferredWidth(w);
                col.setMinWidth(w);
            }
        }
        return tableConstraint;
    }
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTableConstraint());
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        return scrollPane;
    }
    
    /**
     * notify that the path constraints have changed.
     * this should ONLY be called by the model of the path table.
     * @param ok
     */
    protected void changed(boolean ok) {
        but_search.setEnabled(ok);
        but_gp.setEnabled(false);
        but_view.setEnabled(false);
        l_info.setText("");
    }
}
