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
package ops.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ops.DataListener;
import ops.Participant;
import ops.Subscriber;
import ops.data.Message;

public class OpsBinder {
	
	private static Participant part;

	public static void bind(Object partObj) {
		Annotation[] annotations = partObj.getClass().getAnnotations();	
		for (Annotation annotation : annotations) {
			if(annotation.annotationType() == Participate.class) {
				Participate myAnn = (Participate)annotation;
				String domain = myAnn.domain();
				setupDomain(domain);
			}
		}
		
		Method[] methods = partObj.getClass().getMethods();
		for (Method method : methods) {
			Subscribe annotation = method.getAnnotation(Subscribe.class);
			if(annotation != null) {
				String topic = annotation.topic();
				setupSubscription(method, topic, partObj);
			}
		}
		
	}

	private static void setupSubscription(final Method method, String topicName, final Object partObj) {
		GenericTopic topic = part.createGenericTopic(topicName, 1);
		
		Class<?>[] params = method.getParameterTypes();
		if(params.length == 1) {
			Class<?> argType = params[0];
			
			final Subscriber<Message> subscriber = part.createSubscriber(topic, argType);
			subscriber.addDataListener(new DataListener<Message>() {
				
				@Override
				public void onNewData(Message data) {
					try {
						method.invoke(partObj, data);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}
		
		

	}

	private static void setupDomain(String domain) {
		part = Participant.get(domain);
		
	}

}
