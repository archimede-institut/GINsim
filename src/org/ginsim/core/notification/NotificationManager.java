package org.ginsim.core.notification;

import java.util.List;
import java.util.Vector;




public class NotificationManager {

	private static NotificationManager instance;
	
	private List<TopicsListener> notificationListerners;
	
	/**
	 * Default constructor
	 */
	public NotificationManager(){
		
		notificationListerners = new Vector<TopicsListener>();
	}
	
	/**
	 * Give access to the manager instance
	 * 
	 * @return the manager instance
	 */
	public static NotificationManager getInstance(){
		
		if( instance == null){
			instance = new NotificationManager();
		}
		
		return instance;
	}
	
	/**
	 * Register a listener and subscribe it to the given topic
	 * 
	 * @param listener the NotificationListerner to register
	 * @param topic the topic the listener subscribe to
	 */
	public void registerListener( NotificationListener listener, Object topic){
		
		for( TopicsListener topic_listener : notificationListerners){
			if( topic_listener.getListener() == listener){
				topic_listener.addTopic( topic);
				return;
			}
		}
		
		TopicsListener topic_listener = new TopicsListener( listener);
		topic_listener.addTopic( topic);
	}
	
	/**
	 * Publish a notification so NotificationListerner that were registered and
	 * have subscribe to the type of the given notification will add it to their notification list
	 * 
	 * @param message the Notification to publish
	 */
	public void publish( Notification message){
		
		publish( message, false);
	}
	
	/**
	 * Publish a notification so NotificationListerner that were registered and
	 * have subscribe to the type of the given notification will remove it from their notification list
	 * (deletion is due to notification timeout)
	 * 
	 * @param message the Notification to remove
	 */
	public void publishDeletion( Notification message){
		
		publish( message, true);
	}
	
	/**
	 * Publish a notification, sending it to the NotificationListerner that were registered and
	 * have subscribe to the type of the given notification
	 * Id the deletion boolean is false, the NotificationListerner receive the order to add the notification
	 * to its list
	 * If the deletion boolean is true, the NotificationListerner receive the order to remove the notification
	 * from its list
	 * 
	 * @param message the Notification to publish
	 * @param deletion true if the notification must be removed from Notification lists, false if it must be added 
	 */
	private void publish( Notification message, boolean deletion){
		
		for( TopicsListener topic_listener : notificationListerners){
			List<Object> topics_listened = topic_listener.getTopics();
			
			for( Object topic_listened : topics_listened){
				try{
					topic_listened.equals( message.getTopic());
					if( !deletion){
						topic_listener.getListener().receiveNotification( message);
					}
					else{
						topic_listener.getListener().deleteNotification( message);
					}
					break;
				}
				catch( ClassCastException cce){
				}
			}
		}
	}


/**
 * 
 * 
 *
 */
private class TopicsListener{
	
	private NotificationListener listener;
	private List<Object> topics;
	
	public TopicsListener( NotificationListener listener){
		
		this.listener = listener;
	}
	
	/**
	 * Add a topic to the list of topics the listener has subscribed to 
	 * 
	 * @param topic the topic to listen (class inheriting from Notification
	 */
	public void addTopic( Object topic){
		
		if( topics != null && !topics.contains( topic)){
			topics.add( topic);
		}
	}
	
	/**
	 * Give access to the NotificationListener
	 * 
	 * @return the NotificationListener
	 */
	public NotificationListener getListener() {
		
		return listener;
	}
	
	/**
	 * Give access to the list of topics the listener has subscribed to 
	 * 
	 * @return the list of topics the listener has subscribed to 
	 */
	public List<Object> getTopics() {
		
		return topics;
	}
}

}
