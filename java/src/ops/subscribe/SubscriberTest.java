package ops.subscribe;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import ops.Topic;
import ops.examples.FulData;
import ops.examples.SomeData;
import ops.participant.Participant;
import ops.publish.Publisher;
import ops.subscribe.Subscriber.OwnershipKind;

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
