package fr.univmrs.ibdm.GINsim.graph;

/**
 * offer save options to the user.
 */
public interface GsGraphOptionPanel {

    /**
     * @return the save mode selected by the user.
     * @see GsGraph#doSave(String, int, boolean)
     */
	public int getSaveMode();
}
