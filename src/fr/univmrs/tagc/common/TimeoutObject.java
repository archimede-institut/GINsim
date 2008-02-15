package fr.univmrs.tagc.common;

/**
 * Objects waiting for a timeout
 */
public interface TimeoutObject {

    /**
     * the timeout is elapsed
     */
    public void timeout();
}
