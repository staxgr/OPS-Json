/**
*
* Copyright (C) 2011 Anton Gravestam.
*
* This file is part of OPS (Open Publish Subscribe).
*
* OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
*/
package ops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ops.bind.GenericTopic;
import ops.data.Message;

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
