package org.ginsim.gui.annotation.classes.autocomplete;

import java.awt.Point;
import java.util.Map;
import java.util.function.Function;

import javax.swing.text.JTextComponent;

/**
 * Matches entire text instead of separate words
 */
public class TextComponentSuggestionClient implements SuggestionClient<JTextComponent> {

	private Function<String, Map<String, String>> suggestionProvider;

	public TextComponentSuggestionClient(Function<String, Map<String, String>> suggestionProvider) {
		this.suggestionProvider = suggestionProvider;
	}

	@Override
	public Point getPopupLocation(JTextComponent invoker) {
		return new Point(0, invoker.getPreferredSize().height);
	}

	@Override
	public void setSelectedText(JTextComponent invoker, String selectedValue) {
		invoker.setText(selectedValue);
	}
	
	@Override
	public Map<String, String> getSuggestions(JTextComponent invoker) {
		return suggestionProvider.apply(invoker.getText().trim());
	}
}