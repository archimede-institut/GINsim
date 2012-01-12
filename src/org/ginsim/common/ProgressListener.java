package org.ginsim.common;

/**
 * listen for progress messages.
 */
public interface ProgressListener<R> {

    /**
     * @param text
     */
    public void setProgressText(String text);
    
    /**
     * @param result
     */
    public void setResult(R result);
}
