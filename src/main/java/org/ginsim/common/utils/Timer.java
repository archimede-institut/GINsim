package org.ginsim.common.utils;

/**
 * Simple spent-time collector: get current time and return time elapsed since the previous one.  
 * 
 * @author Aurelien Naldi
 */
public class Timer {

	private long timestamp, laststamp;

	/**
	 * Create a timer, and initialize the base timestamp
	 */
	public Timer() {
		reset();
	}
	
	/**
	 * Reset timestamps: as if this object was just created.
	 */
	public void reset() {
		timestamp = System.currentTimeMillis();
		laststamp = timestamp;
	}
	
	/**
	 * Get the time elapsed since the last reset.
	 * This will get the current time and return the difference (in ms)
	 * since the creation (or reset) of this timer.
	 * 
	 * @return the number of ms elapsed since the timer creation.
	 */
	public long stamp() {
		long stamp = System.currentTimeMillis();
		this.laststamp = stamp;
		return stamp-timestamp;
	}
	
	/**
	 * Get the time elapsed since the last timestamp.
	 * This will get the current time and return the difference (in ms)
	 * since the previous measure (reset(), stamp() or shortStamp()).
	 * 
	 * @return the number of ms elapsed since the previous time measure.
	 */
	public long shortStamp() {
		long stamp = System.currentTimeMillis();
		long dt = stamp - laststamp;
		this.laststamp = stamp;
		return dt;
	}
}
