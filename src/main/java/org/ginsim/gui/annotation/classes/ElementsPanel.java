package org.ginsim.gui.annotation.classes;

import java.awt.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.colomoto.biolqm.metadata.annotations.Annotation;
import org.colomoto.biolqm.metadata.annotations.URI;
import org.colomoto.biolqm.metadata.constants.Collection;

/**
 * Display the content of a single (qualified) annotation block,
 * composed of collections of URIs, tags and key:value pairs.
 *
 * @author Martin Boutroux
 * @author Aurelien Naldi
 */
class ElementsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Color SELECTED = Color.ORANGE.brighter().brighter();
	private static final Color NORMAL = Color.GRAY.brighter();

	private static final Border SELECTED_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, SELECTED, SELECTED);
	private static final Border NORMAL_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, NORMAL, NORMAL);

	private boolean selected = false;

	private final Annotation annotation;
	private final JPanel content = new JPanel();
	private final JPanel header = new JPanel();
	private final JLabel qualifierLabel = new JLabel();
	private final JLabel summaryLabel = new JLabel();

	ElementsPanel(Annotation annotation) {
		this.annotation = annotation;
		this.setBorder(NORMAL_BORDER);

		// prepare the header
		header.setBackground(NORMAL);
		header.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,5,2,5);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
//		header.add(new TriangleButton(), gbc);
		gbc.gridx = 2;
		header.add(qualifierLabel, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		header.add(summaryLabel, gbc);

		this.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		content.setLayout(new GridBagLayout());
		this.add(header, gbc);
		gbc.gridy++;
		this.add(content, gbc);

		refresh();
	}

	private void refresh() {
		this.content.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2,2,2,2);
		gbc.anchor = GridBagConstraints.WEST;
		int c = 0;

		if (this.annotation.qualifier == null) {
			this.qualifierLabel.setText("(no qualifier)");
		} else {
			this.qualifierLabel.setText(annotation.qualifier.term);
		}

		summaryLabel.setText(annotation.getShortDescription());

		for (URI uri: annotation.uris) {
			gbc.gridy = c++;
			content.add(ItemLabel.uri(this, uri), gbc);
		}
		for (Map.Entry<String,String> e: annotation.keyValues.entrySet()) {
			gbc.gridy = c++;
			content.add(ItemLabel.key_value(this, e.getKey(), e.getValue()), gbc);
		}
		for (String tag: annotation.tags) {
			gbc.gridy = c++;
			content.add(ItemLabel.tag(this, tag), gbc);
		}
	}

	protected void removeItem(String value) {
		this.annotation.remove(value);
		this.refresh();
	}

	protected void setSelection(Annotation select) {
		if (this.annotation == select) {
			this.setBorder(SELECTED_BORDER);
			this.header.setBackground(SELECTED);
		} else {
			this.setBorder(NORMAL_BORDER);
			this.header.setBackground(NORMAL);
		}
	}
}

enum ItemType {
	URL, KEY_VALUE, TAG
}

class ItemLabel extends JPanel {
	private static final Color BACKGROUND = new Color(200, 200, 255);
	protected static ItemLabel tag(ElementsPanel parent, String tag) {
		return new ItemLabel(parent, ItemType.TAG, "#"+tag);
	}

	protected static ItemLabel uri(ElementsPanel parent, URI uri) {
		Collection col = uri.getCollection();
		if (col == null) {
			return new ItemLabel(parent, ItemType.URL, uri.getValue());
		} else {
			return new ItemLabel(parent, ItemType.URL, col.name + ":" + uri.getValue());
		}
	}

	protected static ItemLabel key_value(ElementsPanel parent, String key, String value) {
		return new ItemLabel(parent, ItemType.KEY_VALUE, key + "=" + value);
	}

	private ItemLabel(ElementsPanel parent, ItemType type, String text) {
		this.setBackground(BACKGROUND);

		CircleButton b_remove = new CircleButton("x", false);
		b_remove.addActionListener(actionEvent -> parent.removeItem(text));
		this.add(b_remove);
		this.add(new JLabel(text));
	}

}
