package org.ginsim.common.callable;

/**
 * Define methods to listen for background task: 
 * Implementors must handle progress messages and the end of the task.
 * 
 * @author Aurelien Naldi
 */
public interface ProgressListener<R> {

    /**
     * Show task status/progress.
     * 
     * @param text
     */
    public void setProgressText(String text);
    
    /**
     * The task finished and returns its result.
     * 
     * @param result
     */
    public void setResult(R result);
}
