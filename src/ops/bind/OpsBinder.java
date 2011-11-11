package ops.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ops.participant.Message;
import ops.participant.Participant;
import ops.subscribe.DataListener;
import ops.subscribe.Subscriber;

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
