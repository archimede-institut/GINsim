package fr.univmrs.ibdm.GINsim.global;

/**
 * listen for progress messages.
 */
public interface GsProgressListener {

    /**
     * @param text
     */
    public void setProgressText(String text);
    
    /**
     * @param result
     */
    public void setResult(Object result);
}
