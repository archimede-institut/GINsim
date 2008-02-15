package fr.univmrs.tagc.common;

/**
 * listen for progress messages.
 */
public interface ProgressListener {

    /**
     * @param text
     */
    public void setProgressText(String text);
    
    /**
     * @param result
     */
    public void setResult(Object result);
}
