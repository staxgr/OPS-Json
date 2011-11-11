package ops.examples;

import ops.Topic;
import ops.participant.Message;
import ops.participant.Participant;
import ops.publish.Publisher;
import ops.subscribe.DataListener;
import ops.subscribe.Subscriber;

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
