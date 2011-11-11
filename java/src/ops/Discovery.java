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
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;

@Path("/discovery")
class Discovery {
	

	private static Map<String, List<SubscriberDescriptor>> subs = new HashMap<String, List<SubscriberDescriptor>>();
	private static Map<String, List<PublisherDescriptor>> pubs = new HashMap<String, List<PublisherDescriptor>>();
	private static Gson gson = new Gson();
	private static List<Wait> waits = new ArrayList<Discovery.Wait>();
	
	
	public static void reset() {
		subs.clear();
		pubs.clear();
		gson = new Gson();
		
	}
	
	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("commit")
	public static String commit(@QueryParam("topic") String topicName, final @QueryParam("message") String jsonData) {
		
		for (Wait wait : waits) {
			if(wait.topicName.equals(topicName)) {
				wait.setData(jsonData);
			}
		}
		
		return gson.toJson(new ClientResponse(true));
		
	}
	
	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("waitData")
	public static String waitData(@QueryParam("topic") String topicName) {
		
		Wait wait = new Wait(topicName);
		waits.add(wait);
			
		return wait.waitForData();
		
	}

//	private static void postSend(final List<SubscriberDescriptor> subsToSendTo, final String jsonData) {
//		for (SubscriberDescriptor sub : subsToSendTo) {
//			sendTo(sub, jsonData);
//		}
//		
//	}

//	private static void sendTo(SubscriberDescriptor sub, String jsonData) {
//				
//	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("getSubscribers")
	public String getSubscribers(@QueryParam("topic") String topic ) {
		System.out.println("Discovery.getSubscribers()");
		String json = "";
		
		gson = new Gson();
		json = gson.toJson(subs.get(topic));
		
		return json;		
	
	}
		
	
//	@GET
//	@Produces("text/html;charset=UTF-8")
//	@Path("registerSubscriber")
//	public String registerSubscriber(@QueryParam("topic") String topic, @QueryParam("topic") String dataType, @QueryParam("address") String address, @QueryParam("port") int port  ) {
//		System.out.println("Discovery.registerSubscriber()");
//				
//		SubscriberDescriptor sub = new SubscriberDescriptor(topic, address, port, dataType);
//		
//		List<SubscriberDescriptor> subList = subs.get(topic);
//		if(subList == null) {
//			subList = new ArrayList<SubscriberDescriptor>();
//			subs.put(topic, subList);
//		}
//		subList.add(sub);	
//		
//		DiscoveryResponse response = new DiscoveryResponse(true);
//		return gson.toJson(response);		
//	
//	}
	
	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("registerSubscriber")
	public String registerSubscriber(@QueryParam("subscriber") String subscriber  ) {
		System.out.println("Discovery.registerSubscriber(json)");
				
		SubscriberDescriptor sub = gson.fromJson(subscriber, SubscriberDescriptor.class);
		
		List<SubscriberDescriptor> subList = subs.get(sub.getTopic());
		if(subList == null) {
			subList = new ArrayList<SubscriberDescriptor>();
			subs.put(sub.getTopic(), subList);
		}
		subList.add(sub);	
		
		ClientResponse response = new ClientResponse(true);
		return gson.toJson(response);
	
	}
	
//	@GET
//	@Produces("text/html;charset=UTF-8")
//	@Path("registerPublisher")
//	public String registerPublisher(@QueryParam("topic") String topic, @QueryParam("address") String address, @QueryParam("port") int port ) {
//		System.out.println("Discovery.registerPublisher()");
//		
//		String jsonRespons = "OK";
//				
//		PublisherDescriptor pub = new PublisherDescriptor(topic, address, port);
//		
//		List<PublisherDescriptor> pubList = pubs.get(topic);
//		if(pubList == null) {
//			pubList = new ArrayList<PublisherDescriptor>();
//			pubs.put(topic, pubList);
//		}
//		pubList.add(pub);	
//		
//		return jsonRespons;		
//	
//	}
	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("registerPublisher")
	public String registerPublisher(@QueryParam("publisher") String publisher ) {
		System.out.println("Discovery.registerPublisher(json)");
				
		PublisherDescriptor pub = gson.fromJson(publisher, PublisherDescriptor.class);
		
		List<PublisherDescriptor> pubList = pubs.get(pub.getTopic());
		if(pubList == null) {
			pubList = new ArrayList<PublisherDescriptor>();
			pubs.put(pub.getTopic(), pubList);
		}
		pubList.add(pub);	
		
		ClientResponse response = new ClientResponse(true);
		return gson.toJson(response);	
	
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("getPublishers")
	public String getPublishers(@QueryParam("topic") String topic ) {
		System.out.println("Discovery.getPublishers()");
		String json = "";
		
		gson = new Gson();
		json = gson.toJson(pubs.get(topic));
		
		return json;
	
	}
	
	
	
	public static class WaitResult {

		private boolean ok;
		private String data;
		
		public WaitResult() {
			
		}
		
		public String getData() {
			return data;
		}

		public boolean isOk() {
			return ok;
		}

		public WaitResult(boolean result, String data) {
			this.ok = result;
			this.data = data;
			
		}

		public WaitResult(boolean result) {
			this.ok = result;
			data = "";
			
		}


	}

	public static class Wait {

		Object mon = new Object();
		private String data;
		boolean dataAvailable = false;
		private final String topicName;
		public Wait(String topicName) {
			this.topicName = topicName;
			
		}

		public String waitForData() {
			synchronized (mon) {
				try {
					mon.wait(5000);
				} catch (InterruptedException e) {

				}
				if (dataAvailable) {
					return Discovery.gson.toJson(new WaitResult(true, data));
				} else {
					return Discovery.gson.toJson(new WaitResult(false));
				}
			
			}			
		}
		public void setData(String data) {
			synchronized (mon) {
				this.data = data;
				dataAvailable = true;
				mon.notify();				
			}
			
		}

	}

	
	

}
