package org.ginsim.servicegui.tool.tbclient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.ginsim.core.graph.view.NodeAttributesReader;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import tbrowser.data.TBProbe;
import tbrowser.data.TBSample;
import tbrowser.data.module.TBModule;
import tbrowser.ihm.widget.TBPanel;

public class TBInfoProfilePanel extends TBPanel implements ChartProgressListener, ActionListener, ListSelectionListener {
  private static final long serialVersionUID = 7691970870575102225L;
  class ProfileTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -4717631575549064932L;
	private final String[] columns = { "", "Gene | Probe", "Value" };
    private Object[][] data;
    private int nbRows;

    public ProfileTableModel(Vector colors, Vector genes, Vector values) {
      super();
      init(colors, genes, values);
    }

    public void init(Vector colors, Vector genes, Vector values) {
      data = new Object[genes.size()][columns.length];
      for (int i = 0; i < genes.size(); i++) {
        data[i][0] = colors.elementAt(i);
        data[i][1] = genes.elementAt(i);
        data[i][2] = values.elementAt(i);
      }
      nbRows = genes.size();
      fireTableDataChanged();
    }

    public int getRowCount() {
      return nbRows;
    }

    public int getColumnCount() {
      return columns.length;
    }

    public String getColumnName(int i) {
      return columns[i];
    }

    public Class getColumnClass(int i) {
      if (i == 0)
        return Color.class;
      else if (i == 2)
        return Double.class;
      return String.class;
    }

    public boolean isCellEditable(int r, int c) {
      return false;
    }

    public Object getValueAt(int r, int c) {
      return data[r][c];
    }

    public void setValueAt(Object object, int r, int c) {
      data[r][c] = object;
    }
  }
  class ColorRenderer extends JLabel implements TableCellRenderer {
 	private static final long serialVersionUID = -1083651294027720671L;
	public ColorRenderer() {
      setOpaque(true);
    }
    public Component getTableCellRendererComponent(JTable table, Object color,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setBackground((Color)color);
      Border b = BorderFactory.createMatteBorder(2, 5, 2, 5, isSelected ? table.getSelectionBackground() : table.getBackground());
      setBorder(b);
      return this;
    }
  }

  private XYPlot plot;
  private XYSeriesCollection dataSet;
  private String[] ticks = new String[0];
  private SampleAxis xAxis = new SampleAxis("Samples", ticks);
  private NumberAxis yAxis = new NumberAxis();
  private XYSplineRenderer itemRenderer = new XYSplineRenderer();
  private ChartPanel chartPanel = new ChartPanel(null);
  private JTable profileTable;
  private ProfileTableModel profileTableModel;
  private TBClientPanel clientPanel;
  private boolean chartProgressLock;

  public TBInfoProfilePanel(TBClientPanel p) {
    super("Profile");
    chartProgressLock = false;
    initGraphic();
    clientPanel = p;
  }
  private void initGraphic() {
    JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    yAxis.setAutoRangeIncludesZero(true);
    xAxis.setAutoRangeIncludesZero(true);
    xAxis.setTickUnit(new NumberTickUnit(1));
    xAxis.setTickLabelsVisible(true);

    itemRenderer.setBaseShapesVisible(false);
    dataSet = new XYSeriesCollection();

    xAxis.setTickLabelInsets(new RectangleInsets(0, 0, 10, 0));
    xAxis.setLabel("Samples");
    yAxis.setLabel("Expression");

    jsp.add(chartPanel, JSplitPane.LEFT);

    profileTableModel = new ProfileTableModel(new Vector(), new Vector(), new Vector());
    profileTable = new JTable(profileTableModel);
    profileTable.getColumnModel().getColumn(0).setCellRenderer(new ColorRenderer());
    profileTable.getColumnModel().getColumn(0).setMaxWidth(25);
    profileTable.getColumnModel().getColumn(0).setMinWidth(25);
    profileTable.getSelectionModel().addListSelectionListener(this);

    TBPanel profileTablePanel = new TBPanel();
    profileTablePanel.addComponent(new JScrollPane(profileTable), 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 0, 5, 0, 0);
    jsp.add(profileTablePanel, JSplitPane.RIGHT);
    addComponent(jsp, 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
  }
  public void init(TBModule m, Vector g) {
    Vector probes = m.getData().getProbes();
    float[][] data = m.getData().getData();
    String symb;
    XYSeries xys;

    dataSet.removeAllSeries();
    ticks = new String[m.getNbSamples()];
    for (int i = 0; i < m.getNbSamples(); i++) ticks[i] = ((TBSample)m.getData().getSamples().elementAt(i)).toString();
    xAxis = new SampleAxis("Samples", ticks);
    plot = new XYPlot(dataSet, xAxis, yAxis, itemRenderer);
    plot.setDomainCrosshairVisible(true);
    plot.setDomainCrosshairValue(m.getNbSamples() / 2);
    plot.setDomainCrosshairLockedOnData(true);
    JFreeChart chart = new JFreeChart(plot);
    chart.removeLegend();
    chart.addProgressListener(this);
    chartPanel.setChart(chart);
    for (int i = 0; i < probes.size(); i++) {
      symb = ((TBProbe)probes.elementAt(i)).getGene().getSymbol().toLowerCase();
      if (g.contains("gene symbol: " + symb)) {
        xys = new XYSeries(symb + " | " + ((TBProbe)probes.elementAt(i)).getID(), false, false);
        for (int j = 0; j < m.getNbSamples(); j++) xys.add(j, data[i][j]);
        dataSet.addSeries(xys);
      }
    }
    plot.setDataset(dataSet);
  }
  public void chartProgress(ChartProgressEvent event) {
  	if (!chartProgressLock) {
    	int old = xAxis.getLabelToDraw();
    Vector v_genes = new Vector();
    Vector v_values = new Vector();
    Vector v_colors = new Vector();
    xAxis.drawLabel((int)plot.getDomainCrosshairValue());
    for (int i = 0; i < dataSet.getSeriesCount(); i++) {
      v_colors.addElement(plot.getLegendItems().get(i).getLinePaint());
      v_genes.addElement(dataSet.getSeries(i).getKey().toString());
      v_values.addElement(new Double(dataSet.getSeries(i).getY(xAxis.getLabelToDraw()).doubleValue()));
    }
    int sel[] = profileTable.getSelectedRows();
    profileTableModel.init(v_colors, v_genes, v_values);
    for (int i = 0; i < sel.length; i++) profileTable.getSelectionModel().addSelectionInterval(sel[i], sel[i]);
    if (old != xAxis.getLabelToDraw()) repaint();
  	}
  }

  public void actionPerformed(ActionEvent actionEvent) {
  	int[] sel = profileTable.getSelectedRows();
    if (sel.length > 0) {
      if (clientPanel != null) {
        NodeAttributesReader vReader = clientPanel.getGraph().getNodeAttributeReader();
        Vector genes = clientPanel.getSelectedGenes();
        for (Iterator it = genes.iterator(); it.hasNext(); ) {
        	vReader.setNode(it.next());
        	vReader.setBackgroundColor(Color.green);
        }
        vReader.refresh();
      }
    }
  }
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			chartProgressLock = true;
			Stroke s1 = new BasicStroke(1);
			Stroke s2 = new BasicStroke(3);
			for (int i = 0; i < profileTable.getRowCount(); i++)
				if (profileTable.isRowSelected(i))
					plot.getRenderer().setSeriesStroke(i, s2);
				else
					plot.getRenderer().setSeriesStroke(i, s1);
			chartProgressLock = false;
		}
	}
}
