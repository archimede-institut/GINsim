package org.ginsim.common.callable;

/**
 * Objects waiting for a timeout
 *
 * @author Aurelien Naldi
 */
public interface TimeoutObject {

    /**
     * the timeout is elapsed
     */
    public void timeout();
}
