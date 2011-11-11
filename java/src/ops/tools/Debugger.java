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
package ops.tools;

import ops.DataListener;
import ops.Participant;
import ops.Publisher;
import ops.Subscriber;
import ops.Topic;
import ops.data.Message;

public class Debugger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String domain = null;
		boolean publish = false;

		if (args.length < 1) {
			System.out.println("Usage (anything else will crash...):");
			System.out.println("Subscribe: <domain> <topic name>:<topic port>*");
			System.out.println("Publish:   -p <domain> <topic name>:<topic port> '<json string>'");
			System.exit(1);
		}
		if (args[0].equals("-p")) {
			domain = args[1];
			publish = true;

		} else {
			domain = args[0];
		}

		Participant part = Participant.get(domain);

		if (publish) {
			String[] split = args[2].split(":");
			Topic<Message> topic = part.createTopic(split[0],
					Integer.parseInt(split[1]));
			Publisher<Message> pub = part.createPublisher(topic);
			pub.writeRaw(args[3]);

		} else {

			for (int i = 1; i < args.length; i++) {
				String[] split = args[i].split(":");
				Topic<Message> topic = part.createTopic(split[0],
						Integer.parseInt(split[1]));
				Subscriber<Message> subscriber = part.createSubscriber(topic,
						Message.class);
				subscriber.printOnly();
				subscriber.addDataListener(new DataListener<Message>() {

					@Override
					public void onNewData(Message data) {
						// Do nothing
					}
				});

			}
		}

	}

}
