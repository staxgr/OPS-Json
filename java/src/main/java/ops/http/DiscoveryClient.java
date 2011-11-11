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
package ops.http;

import java.lang.reflect.Type;
import java.util.List;

import ops.http.Discovery.WaitResult;

import com.google.gson.reflect.TypeToken;


class DiscoveryClient extends GsonRestClient {
	

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
