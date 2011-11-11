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
package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import ops.DataListener;
import ops.DeadlineListener;
import ops.Participant;
import ops.Publisher;
import ops.Subscriber;
import ops.Subscriber.OwnershipKind;
import ops.Topic;
import ops.examples.FulData;
import ops.examples.SomeData;

import org.junit.BeforeClass;
import org.junit.Test;

public class SubscriberTest {

	private static Participant part;
	private static Topic<SomeData> someTopic;
	
	private boolean deadlineMissed = false;
	private List<SomeData> samples = new ArrayList<SomeData>();
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		part = Participant.get("ops.mc://235.4.3.2:12000");
		someTopic = part.createTopic("TestTopic", 1);
		
	}
	
	@Test
	public void testPubSub() throws InterruptedException {
		final Subscriber<SomeData> subscriber = part.createSubscriber(someTopic, SomeData.class);
		subscriber.setDeadlineTimeout(1000);
		subscriber.addDeadlineListener(new DeadlineListener() {
			
			@Override
			public void onDeadlineMissed(Subscriber<?> subscriber) {
				System.out
						.println("onDeadlineMissed()");
				deadlineMissed = true;
				subscriber.setDeadlineTimeout(5000);
				
			}
		});
		subscriber.addDataListener(new DataListener<SomeData>() {
			
			@Override
			public void onNewData(SomeData data) {
				System.out
						.println("onNewData()");
				samples.add(data);
				
			}
		});
		
		Publisher<SomeData> publisher = part.createPublisher(someTopic);
		
		SomeData data = new SomeData();
		data.is.add(1);
		data.is.add(2);
		data.is.add(3);
		data.key = "tratt";
		data.ful = new FulData();
		data.ful.i = 0;
		data.ful.s = "hatt";
		
		while(data.i < 100) {
			
			publisher.write(data);
			// Make some modifications to the data for next publication
			data.i++;
			
			Thread.sleep(1);
			
		}
		
		for (int i = 0; i < 100; i++) {
			assertEquals(i, samples.get(i).i);
		}
		
		samples.clear();
		
		
		
	}
	
	@Test
	public void testExclusiveOwnership() throws InterruptedException {
		final Subscriber<SomeData> subscriber = part.createSubscriber(someTopic, SomeData.class);
		subscriber.setDeadlineTimeout(2000);
		subscriber.setOwnershipKind(OwnershipKind.EXCLUSIVE);
		subscriber.addDeadlineListener(new DeadlineListener() {
			
			@Override
			public void onDeadlineMissed(Subscriber<?> subscriber) {
				System.out
						.println("onDeadlineMissed()");
				deadlineMissed = true;
				
				
			}
		});
		subscriber.addDataListener(new DataListener<SomeData>() {
			
			@Override
			public void onNewData(SomeData data) {
				System.out
						.println("onNewData() " + data.publisherName);
				samples.add(data);
				
			}
		});
		
		Publisher<SomeData> publisher1 = part.createPublisher(someTopic);
		Publisher<SomeData> publisher2 = part.createPublisher(someTopic);
		
		publisher1.setName("pub1");
		publisher2.setName("pub2");
		
		
		SomeData data1 = new SomeData();
		data1.key = "tratt1";
		data1.ful = new FulData();
		data1.ful.s = "hatt1";
		data1.strength = 1;
		
		SomeData data2 = new SomeData();
		data2.key = "tratt2";
		data2.ful = new FulData();
		data2.ful.s = "hatt2";
		data2.strength = 2;
		
		while(data1.i < 100) {
			
			Thread.sleep(100);
			
			if(data1.i % 10 == 0) {
				publisher2.write(data2);
				data2.i++;
			}
			
			
			// Make some modifications to the data for next publication
			data1.i++;
			
			
		}
		
		for (SomeData data : samples) {
			assertEquals("pub2", data.publisherName);
		}
		
		samples.clear();
		
		Thread.sleep(2500);
		publisher1.write(data1);
		Thread.sleep(2500);
		
		assertEquals(1, samples.size());
		
		for (SomeData data : samples) {
			assertEquals("pub1", data.publisherName);
		}
		
			
	}

	@Test
	public void testSetDeadlineTimeout() throws InterruptedException {
		
		final Subscriber<SomeData> subscriber = part.createSubscriber(someTopic, SomeData.class);
		subscriber.setDeadlineTimeout(1000);
		subscriber.addDeadlineListener(new DeadlineListener() {
			
			@Override
			public void onDeadlineMissed(Subscriber<?> subscriber) {
				System.out
						.println("onDeadlineMissed()");
				deadlineMissed = true;
				subscriber.setDeadlineTimeout(5000);
				
			}
		});
		
		Thread.sleep(100);
		assertEquals(false, deadlineMissed);
		
		Thread.sleep(1000);
		assertEquals(true, deadlineMissed);
		deadlineMissed = false;
		
		Thread.sleep(3000);
		assertEquals(false, deadlineMissed);
		
		Thread.sleep(3000);
		assertEquals(true, deadlineMissed);
		
		
	}

}
