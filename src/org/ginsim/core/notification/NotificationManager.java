package org.ginsim.core.notification;

import java.util.List;
import java.util.Vector;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.notification.resolvable.ResolvableErrorNotification;
import org.ginsim.core.notification.resolvable.ResolvableWarningNotification;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;




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
	private static NotificationManager getInstance(){
		
		if( instance == null){
			instance = new NotificationManager();
		}
		
		return instance;
	}
	
	/**
	 * Register a listener and subscribe it to the given topic (static call)
	 * 
	 * @param listener the NotificationListerner to register
	 * @param topic the topic the listener subscribe to
	 */
	public static void registerListener( NotificationListener listener, Object topic){
		
		getInstance().register( listener, topic);
	}
	
	/**
	 * Register a listener and subscribe it to the given topic
	 * 
	 * @param listener the NotificationListerner to register
	 * @param topic the topic the listener subscribe to
	 */
	private void register( NotificationListener listener, Object topic){
		
		for( TopicsListener topic_listener : notificationListerners){
			if( topic_listener.getListener() == listener){
				topic_listener.addTopic( topic);
				return;
			}
		}
		
		TopicsListener topic_listener = new TopicsListener( listener);
		topic_listener.addTopic( topic);
		notificationListerners.add( topic_listener);
	}
	
	/**
	 * Publish an error message
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 */
	public static void publishError( Object topic, String message){
		
		if( topic != null && message != null){
			getInstance().publish( new ErrorNotification( topic, message));
		}
	}
	
	/**
	 * Publish a Java exception 
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 * @param exception the Exception to publish
	 */
	public static void publishException( Object topic, String message, Exception exception){
		
		if( topic != null && message != null){
			getInstance().publish( new ExceptionNotification( topic, message, exception));
		}
	}
	
	/**
	 * Publish a warning message
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 */
	public static void publishWarning( Object topic, String message){
		
		if( topic != null && message != null){
			getInstance().publish( new WarningNotification( topic, message));
		}
	}
	
	/**
	 * Publish an information message
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 */
	public static void publishInformation( Object topic, String message){
		
		if( topic != null && message != null){
			getInstance().publish( new InformationNotification( topic, message));
		}
	}
	
	/**
	 * Publish an error message with its resolution options 
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 * @param graph the graph concerned by the notification
	 * @param data the data required for the 
	 * @param resolution the NotificationResolution containing the resolution options
	 */
	public static void publishResolvableError( Object topic, String message, Graph graph, Object[] data, NotificationResolution resolution){
		
		if( topic != null && message != null){
			getInstance().publish( new ResolvableErrorNotification( topic, message, graph, data, resolution));
		}
	}
	
	
	/**
	 * Publish a warning message with its resolution options 
	 * 
	 * @param topic the topic of the notification
	 * @param message the message of the notification
	 * @param graph the graph concerned by the notification
	 * @param data the data required for the 
	 * @param resolution the NotificationResolution containing the resolution options
	 */
	public static void publishResolvableWarning( Object topic, String message, Graph graph, Object[] data, NotificationResolution resolution){
		
		if( topic != null && message != null){
			getInstance().publish( new ResolvableWarningNotification( topic, message, graph, data, resolution));
		}
	}
	
	
	/**
	 * Publish a notification so NotificationListerner that were registered and
	 * have subscribe to the type of the given notification will add it to their notification list
	 * 
	 * @param message the Notification to publish
	 */
	private void publish( Notification message){
		
		publish( message, false);
	}
	
	/**
	 * Publish a notification so NotificationListerner that were registered and
	 * have subscribe to the type of the given notification will remove it from their notification list
	 * (deletion is due to notification timeout)
	 * 
	 * @param message the Notification to remove
	 */
	public static void publishDeletion( Notification message){
		
		getInstance().publish( message, true);
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
		topics = new Vector<Object>();
	}
	
	/**
	 * Add a topic to the list of topics the listener has subscribed to 
	 * 
	 * @param topic the topic to listen (class inheriting from Notification
	 */
	public void addTopic( Object topic){
		
		if( topic != null && !topics.contains( topic)){
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
	
	@Override
	public String toString() {
		
		return "Notificationlistener : " + listener + "->" + topics;
	}
}

}
