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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;
import java.util.List;

import ops.http.Discovery.WaitResult;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

public class DiscoveryClientTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		final URL warUrl = Discovery.class.getClassLoader().getResource("ops/http");
        try {
        	WebServer.init();
            WebServer.addWebApp("/ops", warUrl.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private DiscoveryClient client;

	@Before
	public void setUp() throws Exception {
		client = new DiscoveryClient("http://localhost:9090/ops/discovery/");
		Discovery.reset();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegisterSubscriber() {
		
		String dataType = "FooData";
		int port = 8989;
		String address = "1.1.1.1";
		String topic = "TestTopic";
		
		SubscriberDescriptor descriptor = new SubscriberDescriptor(topic, address, port, dataType);
		
		ClientResponse resp = client.registerSubscriber(descriptor);
		
		assertNotNull(resp);
		assertEquals(true, resp.isOk());
		
		List<SubscriberDescriptor> subscribers = client.getSubscribers(topic);
		assertEquals(1, subscribers.size());
		
		SubscriberDescriptor sub = subscribers.get(0);
		
		assertEquals(topic, sub.getTopic());
		assertEquals(dataType, sub.getDataType());
		assertEquals(address, sub.getAddress());
		
	}

	@Test
	public void testRegisterPublisher() {

		int port = 8989;
		String address = "1.1.1.1";
		String topic = "TestTopic";
		
		PublisherDescriptor descriptor = new PublisherDescriptor(topic, address, port);
		
		ClientResponse resp = client.registerPublisher(descriptor);
		
		assertNotNull(resp);
		assertEquals(true, resp.isOk());
		
		List<PublisherDescriptor> publishers = client.getPublishers(topic);
		assertEquals(1, publishers.size());
		
		PublisherDescriptor pub = publishers.get(0);
		
		assertEquals(topic, pub.getTopic());
		assertEquals(port, pub.getPort());
		assertEquals(address, pub.getAddress());
	}
	
	@Test
	public void testGetUnexistingPublisher() {

		int port = 8989;
		String address = "1.1.1.1";
		String topic = "TestTopic";
		
		PublisherDescriptor descriptor = new PublisherDescriptor(topic, address, port);
		
		ClientResponse resp = client.registerPublisher(descriptor);
		
		assertNotNull(resp);
		assertEquals(true, resp.isOk());
		
		List<PublisherDescriptor> publishers = client.getPublishers("FOO");
		assertNull(publishers);
	

	}
	
	@Test
	public void testGetUnexistingSubscriber() {
		
		String dataType = "FooData";
		int port = 8989;
		String address = "1.1.1.1";
		String topic = "TestTopic";
		
		SubscriberDescriptor descriptor = new SubscriberDescriptor(topic, address, port, dataType);
		
		ClientResponse resp = client.registerSubscriber(descriptor);
		
		assertNotNull(resp);
		assertEquals(true, resp.isOk());
		
		List<SubscriberDescriptor> subscribers = client.getSubscribers("FOO");
		
		assertNull(subscribers);
		
	}
	
	@Test
	public void testWait() throws InterruptedException {
		final String topic = "TestTopic";
		final FooData data = new FooData();
		final WaitResult waitResult = client.waitData(topic);
		
		// Check that wait times out if no data
		assertEquals(false, waitResult.isOk());
	
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					// Delay commit so that we wait for data before commit
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.commit(topic, data);				
			}
		}).start();
		
		
		// This should receive the data commit above
		final WaitResult waitResult2 = client.waitData(topic);
		assertEquals(true, waitResult2.isOk());
		assertEquals(data, new Gson().fromJson(waitResult2.getData(), FooData.class));
		System.out.println(waitResult2.getData());
		
		
		
	}
	
	public static class FooData {
		int i = 56;
		String s = "Text";
		
		public FooData() {

		}
		public int getI() {
			return i;
		}
		public String getS() {
			return s;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FooData other = (FooData) obj;
			if (i != other.i)
				return false;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			return true;
		}
		
		
		
		
		
	}
	

}
