package org.ginsim.gui.graph.view.style;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.application.OptionStore;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;


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
	
    protected StyleProvider style;
	private String storeUserChoicePrefix;

	private JCheckBox cb_colorize;


    private final StyleManager styleManager;

	/**
	 * Create a ColorizerPanel containing the colorizeButton.
	 * If addInitialColorizationCheckbox is true, also add the initialColorizationCheckbox.
	 * 
	 * @param storeUserChoicePrefix define the prefix to store the user preference on the initial checkbox state (default false)
	 */
	public ColorizerPanel(String storeUserChoicePrefix, Graph<?, ?> graph) {
		this.storeUserChoicePrefix = storeUserChoicePrefix;
        this.styleManager = graph.getStyleManager();
        initialize();
	}

	/**
	 * Create a ColorizerPanel containing the colorizeButton but no initialColorizationCheckbox.
	 * If addInitialColorizationCheckbox is true, also add the initialColorizationCheckbox.
	 */
	public ColorizerPanel(Graph<?, ?> graph) {
		this("", graph);
	}

	/**
	 * Create the content of the panel
	 */
	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
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
		if (cb_colorize == null) {
			cb_colorize = new JCheckBox(Translator.getString("STR_colorizer_panel"));
			cb_colorize.setSelected((OptionStore.getOption(storeUserChoicePrefix+OPTION_STORE_INITIAL_COLORIZATION, Boolean.FALSE)).booleanValue());
			cb_colorize.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					boolean b = cb_colorize.isSelected();
					if (style != null && b) {
						styleManager.setStyleProvider(style);
					} else {
                        styleManager.setStyleProvider(null);
                    }
					OptionStore.setOption(storeUserChoicePrefix+OPTION_STORE_INITIAL_COLORIZATION, Boolean.valueOf(b));
				}
		    });
			this.add(cb_colorize, c);
		}
	}
	
	/**
	 * Perform the colorization of the graph, update the button name accordingly
	 */
	public void doColorize() {
		styleManager.setStyleProvider(style);
		cb_colorize.setSelected(true);
	}

	/**
	 * Cancel the colorization of the graph, update the button name accordingly
	 */
	public void undoColorize() {
        styleManager.setStyleProvider(null);
        cb_colorize.setSelected(false);
	}
	
	/**
	 * Indicates if the initialColorizationCheckbox is checked or not
	 * @return the state of the initialColorizationCheckbox
	 */
	protected boolean shouldColorizeInitially() {
		return cb_colorize.isSelected();
	}

    /**
     * Set the style and enable the colorizeButton. If initialColorizationCheckbox is
     * used and checked, then the colorization is launched.
     * @param style the style returned by the algorithm
     */
    public void setStyle(StyleProvider style) {
        this.style = style;
        if (cb_colorize.isSelected()) {
            styleManager.setStyleProvider(style);
        }
        runIsFinished();
    }


    /**
	 * Enable the colorizeButton. If initialColorizationCheckbox is
	 * used and checked, then the colorization is launched.
	 */
	public void runIsFinished() {
		if (shouldColorizeInitially()) {
			doColorize();
		}
	}
	
}
