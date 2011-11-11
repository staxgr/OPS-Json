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
package ops.examples;

import ops.DataListener;
import ops.Participant;
import ops.Publisher;
import ops.Subscriber;
import ops.Topic;
import ops.filters.KeyFilteredDataListener;

public class Publish {
	
	public static void main(String[] args) throws InterruptedException {
		
		
		// Setup a participant with multicast transport on a multicast address and a base port.
		Participant part = Participant.get("ops.mc://235.4.3.2:12000");
		
		// Create a topic with a name and a port, in the case of multicast participant, an offset from the base port.
		// Topic name-port combinations shall be configurable or statically defined in a system.
		// Ports can be reused by several topics. Use many different ports for best network usage and few to save ports 
		// and application threads.
		Topic<SomeData> someTopic = part.createTopic("TestTopic", 1);
		
		//Topic<SomeData> someTopic2 = part.createTopic("TestTopic2", 2);
		
		// Create a publisher for the topic
		Publisher<SomeData> publisher = part.createPublisher(someTopic);
		
		// Create a subscriber for the topic
		Subscriber<SomeData> subscriber = part.createSubscriber(someTopic, SomeData.class);
		
		// Add a DataListener to your subscriber to get a callback when new data arrives.
		// In this case a filtered one on key.
		subscriber.addDataListener(new KeyFilteredDataListener<SomeData>("hatt", new DataListener<SomeData>() {
			public void onNewData(SomeData data) {
				System.out.println("Hello " + data.i);				
			}
		}));
		
		// Create some data to publish
		SomeData data = new SomeData();
		data.is.add(1);
		data.is.add(2);
		data.is.add(3);
		data.key = "tratt";
		data.ful = new FulData();
		data.ful.i = 56;
		data.ful.s = "hatt";
		
		while(true) {
			
			publisher.write(data);
			
			// Make some modifications to the data for next publication
			data.i++;
			if(data.i > 2) {
				data.key = "hatt";
			}
			if(data.i > 10) {
				part.removeSubsriber(subscriber);
				boolean stop = part.stop();
				System.out.println("Stop OK = " + stop);
				break;
			}
			//data.is.add(data.i);
			Thread.sleep(500);
			
		}
		
	}

}
