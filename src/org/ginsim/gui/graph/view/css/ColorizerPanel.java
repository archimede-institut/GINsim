package org.ginsim.gui.graph.view.css;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.OptionStore;
import org.ginsim.common.utils.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.Colorizer;



/**
 * JPanel to control the colorization of a graph via a GUI.
 * It handle properly the save of the original colors of the graph.
 * The colorizeButton allows to apply the new computed color or restore the original color.
 * 
 * Usage : 
 *    Call the constructor with the name of your service and a boolean indicating if you want 
 *    to apply the colorization automatically after the algorithm has finish running.
 *    
 *    Right after your algorithm has finish running, call setNewColorizer(Colorizer) with the 
 *    colorizer given by the algorithm.
 *    
 *    When the window containing this panel is closing, call frameIsClosing() to ask the user if he want to 
 * restore the original color if needed, and return false if the user pressed the cancel button.
 *
 */
public class ColorizerPanel extends JPanel {
	private static final long serialVersionUID = -1457727191025051650L;
	
	private static String OPTION_STORE_INITIAL_COLORIZATION = ".colorizer.initial_colorization";
	
	protected Colorizer colorizer;
	private String storeUserChoicePrefix;
	private boolean addInitialColorizationCheckbox;

	private JCheckBox initialColorizationCheckbox;
	private JButton colorizeButton;


	/**
	 * The graph to colorize
	 */
	private Graph<?, ?> graph;

	/**
	 * Create a ColorizerPanel containing the colorizeButton.
	 * If addInitialColorizationCheckbox is true, also add the initialColorizationCheckbox.
	 * 
	 * @param storeUserChoicePrefix define the prefix to store the user preference on the initial checkbox state (default false)
	 * @param addInitialColorizationCheckbox indicates if the initialColorizationCheckbox should be added to the panel
	 */
	public ColorizerPanel(boolean addInitialColorizationCheckbox, String storeUserChoicePrefix, Graph<?, ?> graph) {
		this.addInitialColorizationCheckbox = addInitialColorizationCheckbox;
		this.storeUserChoicePrefix = storeUserChoicePrefix;
		this.graph = graph;
        initialize();
	}

	/**
	 * Create a ColorizerPanel containing the colorizeButton but no initialColorizationCheckbox.
	 * If addInitialColorizationCheckbox is true, also add the initialColorizationCheckbox.
	 */
	public ColorizerPanel(Graph<?, ?> graph) {
		this(false, "", graph);
	}

	/**
	 * Create the content of the panel
	 */
	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		init_colorizeButton(c);
		c.gridy++;
		c.insets = new Insets(0, 20, 0, 0);
		init_initialColorizationCheckbox(c);
	}

	/**
	 * Create the init_initialColorizationCheckbox and add it to the panel
	 * 
	 * @param c GridBagConstraints to place the element
	 */
	private void init_initialColorizationCheckbox(GridBagConstraints c) {
		if (addInitialColorizationCheckbox && initialColorizationCheckbox == null) {
			initialColorizationCheckbox = new JCheckBox(Translator.getString("STR_colorizer_initialColorizarionCheckbox"));
			initialColorizationCheckbox.setSelected(((Boolean)OptionStore.getOption(storeUserChoicePrefix+OPTION_STORE_INITIAL_COLORIZATION, Boolean.FALSE)).booleanValue());
			initialColorizationCheckbox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					boolean b = initialColorizationCheckbox.isSelected();
					OptionStore.setOption(storeUserChoicePrefix+OPTION_STORE_INITIAL_COLORIZATION, Boolean.valueOf(b));
				}
		    });
			this.add(initialColorizationCheckbox, c);
		}
	}
	
	/**
	 * Create the colorizeButton and add it to the panel
	 * 
	 * @param c GridBagConstraints to place the element
	 */
	private void init_colorizeButton(GridBagConstraints c) {
		if (colorizeButton == null) {
		    colorizeButton = new JButton(Translator.getString("STR_colorizer_do_colorize"));
		    colorizeButton.setEnabled(false);
		    colorizeButton.addActionListener(new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (colorizer.isColored()) {
						undoColorize();
					} else {
						doColorize();
					}
				}
			});
		    this.add(colorizeButton, c);			
		}
	}

	/**
	 * Perform the colorization of the graph, update the button name accordingly
	 */
	protected void doColorize() {
		colorizer.doColorize(graph);
		colorizeButton.setText(Translator.getString("STR_colorizer_undo_colorize"));
	}

	/**
	 * Cancel the colorization of the graph, update the button name accordingly
	 */
	protected void undoColorize() {
		colorizer.undoColorize(graph);
		colorizeButton.setText(Translator.getString("STR_colorizer_do_colorize"));
	}
	
	/**
	 * Indicates if the initialColorizationCheckbox is checked or not
	 * @return the state of the initialColorizationCheckbox
	 */
	protected boolean shouldColorizeInitially() {
		return addInitialColorizationCheckbox && initialColorizationCheckbox.isSelected();
	}

	/**
	 * Set the colorizer and enable the colorizeButton. If initialColorizationCheckbox is
	 * used and checked, then the colorization is launched.
	 * @param colorizer the colorizer returned by the algorithm
	 */
	public void setNewColorizer(Colorizer colorizer) {
		if (this.colorizer != null && colorizer.isColored()) {
			undoColorize();
		}
		this.colorizer = colorizer;
		runIsFinished();
	}
	
	/**
	 * Enable the colorizeButton. If initialColorizationCheckbox is
	 * used and checked, then the colorization is launched.
	 */
	public void runIsFinished() {
		colorizeButton.setEnabled(true);
		if (shouldColorizeInitially()) doColorize();
	}
	
	
	/**
	 * If the graph is still colored, ask the user if he wants to restore the original 
	 * color before closing. If the user click on YES, the color is restored. Then 
	 * return true if he clicks on YES or NO. false if he click on CANCEL, meaning the
	 * windows should not be closed.
	 * 
	 * @return true if the frame can be closed, false if the CANCEL button has been pressed
	 */
	public boolean frameIsClosing() {
		if (colorizer.isColored()) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_colorizer_sure_close_undo_colorize"));
			if (res == JOptionPane.CANCEL_OPTION) {
				return false;
			} else if (res == JOptionPane.YES_OPTION) {
				undoColorize();
			}
		}
		return true;
	}

	
}
