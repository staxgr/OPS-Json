/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops.participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ops.Sender;
import ops.Topic;
import ops.bind.GenericTopic;
import ops.publish.Publisher;
import ops.subscribe.Subscriber;

/**
 *
 * @author Anton
 */
public abstract class Participant {
    private static HashMap<String, Participant> instances = new HashMap<String, Participant>();

    //private static Logger logger = Logger.getLogger(Participant.class.getName());

    public static Participant get(String domainDescriptor)
    {
        
        Participant part = instances.get(domainDescriptor);
        if(part== null) {
            part = ParticipantFactory.get(domainDescriptor);
            if(part == null) {
                throw new RuntimeException("No participant available for " + domainDescriptor);
            }
            instances.put(domainDescriptor, part);
        }

        return part;
    }
    
    protected List<Subscriber<?>> subscribers = new ArrayList<Subscriber<?>>();

    
    
    

    public synchronized boolean addSubscriber(Subscriber<?> e) {
		return subscribers.add(e);
	}

	public synchronized boolean removeSubsriber(Subscriber<?> o) {
		return subscribers.remove(o);
	}

	public abstract Sender getSender();
    public abstract <T> Topic<T> createTopic(String name, int port);

	public abstract <E extends Message> Subscriber<E> createSubscriber(Topic<E> topic, Class<?> cls);

	public abstract <E extends Message> Publisher<E> createPublisher(Topic<E> topic);
	
	public abstract boolean stop();

	public GenericTopic createGenericTopic(String topicName, int port) {
		return new GenericTopic(topicName, port);
	}

}
