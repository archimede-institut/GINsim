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
    /**
     * @return true if associated parameters should also be saved
     */
    public boolean isExtended() ;
    /**
     * @return the extension for the saved file
     */
    public String getExtension();
    /**
     * @return true if the extended file should be compressed
     */
	public boolean isCompressed();
}
