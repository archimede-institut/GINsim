package org.ginsim.gui.graph.view.css;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;


/**
 * JPanel to control the colorization of a graph via a GUI.
 * It overrides the graph's style with a StyleProvider
 */
public class StyleColorizerCheckbox extends JCheckBox {
	
	protected final StyleManager styleManager;
	protected final StyleProvider styleProvider;

	/**
	 * Create a ColorizerPanel containing the colorizeButton.
	 * If addInitialColorizationCheckbox is true, also add the initialColorizationCheckbox.
	 * 
	 * @param storeUserChoicePrefix define the prefix to store the user preference on the initial checkbox state (default false)
	 * @param styleProvider the style to apply when the checkbox is selected
	 */
	public StyleColorizerCheckbox(String storeUserChoicePrefix, Graph<?, ?> graph, StyleProvider styleProvider) {
		super(Translator.getString("STR_colorizer_panel"));
		this.styleManager = graph.getStyleManager();
		this.styleProvider = styleProvider;

		setSelected(true);
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean b = isSelected();
				if (b) {
					doColorize();
				} else {
					undoColorize();
				}
			}
	    });
	}
	
	/**
	 * Perform the colorization of the graph, update the button name accordingly
	 */
	public void doColorize() {
		styleManager.setStyleProvider(styleProvider);
	}

	/**
	 * Cancel the colorization of the graph, update the button name accordingly
	 */
	public void undoColorize() {
		styleManager.setStyleProvider(null);
	}
	
	public void refresh() {
		if (isSelected()) {
			styleManager.setStyleProvider(styleProvider);
		}
	}
}
