package fr.univmrs.tagc.common;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.SimpleDialog;

/**
 * A small dialog to display a textarea with a close button.
 *
 */
public class ResultsDialog extends SimpleDialog {
	private static final long serialVersionUID = -4012429687480688324L;
	private JPanel mainPanel;
	private String results;
	private JTextArea resultsPane;

	public ResultsDialog(Frame parent, String results) {
		super(parent, Translator.getString("STR_results"), 300, 400);
		this.results = results;
		
        initialize();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				cancel();
			}
		});
	}
	
	public ResultsDialog(Frame parent) {
		this(parent, "");
	}

	protected void initialize() {
		JPanel content = getContentPanel();
		setContentPane(content);
	    content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
	    content.getActionMap().put("ESCAPE", new AbstractAction() {
			private static final long serialVersionUID = 448859746054492959L;
			public void actionPerformed(ActionEvent actionEvent) {
				cancel();
			}
		});
	    setVisible(true);
	}
	
	private JPanel getContentPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
		//Label
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.weighty = 1.0;
			resultsPane = new JTextArea(results);
	        JScrollPane resultsScrollPane = new JScrollPane(resultsPane);
	        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        resultsScrollPane.setPreferredSize(new Dimension(250, 250));
			mainPanel.add(resultsScrollPane, c);
			
			c.gridy++;
			JButton bclose = new javax.swing.JButton(Translator.getString("STR_close"));
        	bclose.setToolTipText(Translator.getString("STR_closedialog_descr"));
        	bclose.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cancel();
                }
            });
			mainPanel.add(bclose, c);

		}
		return mainPanel;
	}	
	
	public void doClose() {
		cancel();
	}	
	protected void cancel() {
		setVisible(false);
	}
	
	
	public String getResults() {
		return results;
	}
	
	/**
	 * Define the results and put it in the textarea
	 * @param results
	 */
	public void setResults(String results) {
		if (resultsPane != null) resultsPane.setText(results);
		this.results = results;
	}
}
