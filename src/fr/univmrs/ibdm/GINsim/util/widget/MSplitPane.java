package fr.univmrs.ibdm.GINsim.util.widget;

import javax.swing.JSplitPane;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

public class MSplitPane extends JSplitPane {
	private static final long serialVersionUID = -3317281733034600402L;
	
	public MSplitPane(String id) {
		super();
		if (id != null) {
			Object o = GsOptions.getOption(id+"."+DIVIDER_LOCATION_PROPERTY);
			if (o != null && o instanceof Integer) {
				setDividerLocation(((Integer)o).intValue());
			}
			addPropertyChangeListener(DIVIDER_LOCATION_PROPERTY, new PropertyListenAndSave(id));
		}
	}
}
