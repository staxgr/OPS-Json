package ops.participant.http;

import java.lang.reflect.Type;
import java.util.List;

import ops.participant.http.Discovery.WaitResult;

import com.google.gson.reflect.TypeToken;


public class DiscoveryClient extends GsonRestClient {
	

	public DiscoveryClient(String baseUrl) {
		super(baseUrl);
	}
	
	public synchronized List<SubscriberDescriptor> getSubscribers(String topic) {
		Type listType = new TypeToken<List<SubscriberDescriptor>>() {}.getType();
		return executeGet("getSubscribers", listType, param("topic", topic));		
	}

	public synchronized List<PublisherDescriptor> getPublishers(String topic) {
		Type listType = new TypeToken<List<PublisherDescriptor>>() {}.getType();
		return executeGet("getPublishers", listType, param("topic", topic));	
	}
	
	public synchronized ClientResponse registerSubscriber(SubscriberDescriptor sub) {
		return executeGet("registerSubscriber", ClientResponse.class, param("subscriber", gson.toJson(sub)));
	}
	
	public synchronized ClientResponse registerPublisher(PublisherDescriptor pub) {
		return executeGet("registerPublisher", ClientResponse.class, param("publisher", gson.toJson(pub)));
	}
	
	public ClientResponse commit(String topic, Object data) {
		return executeGet("commit", ClientResponse.class, param("topic", topic), param("message", gson.toJson(data)));
	}
	
	public WaitResult waitData(String topic) {
		return executeGet("waitData", WaitResult.class, param("topic", topic));
	}
	
	
	
	
	
	

}
