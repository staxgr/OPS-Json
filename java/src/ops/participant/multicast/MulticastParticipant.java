/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops.participant.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import ops.MulticastSender;
import ops.MulticastSocketCreator;
import ops.Sender;
import ops.Topic;
import ops.participant.EmptyMessage;
import ops.participant.Message;
import ops.participant.Participant;
import ops.participant.Receiver;
import ops.participant.ReceiverListener;
import ops.publish.Publisher;
import ops.subscribe.Subscriber;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Anton
 */
public class MulticastParticipant extends Participant implements
		ReceiverListener {

	public static final int DEFAULT_TTL = 1;
	
	final MulticastSender sender;

	private final int basePort;

	private Map<Integer, Receiver> receivers = new HashMap<Integer, Receiver>();

	private final String address;

	public MulticastParticipant(String address, int basePort)
			throws IOException {
		this.address = address;
		this.basePort = basePort;
		sender = new MulticastSender(basePort, address, "0.0.0.0", 2, 0);

	}

	public boolean stop() {
		boolean stopOK = true;
		for (Receiver receiver : receivers.values()) {
			if (!receiver.stop()) {
				stopOK = false;
			}
		}
		receivers.clear();
		return stopOK;
	}

	@Override
	public synchronized <E extends Message> Publisher<E> createPublisher(
			Topic<E> topic) {
		return new Publisher<E>(topic, this);
	}

	@Override
	public synchronized <E extends Message> Subscriber<E> createSubscriber(
			Topic<E> topic, Class<?> cls) {

		if (!receivers.containsKey(topic.getPort())) {
			createReceiver(topic);

		}

		return new Subscriber<E>(topic, this, cls);
	}

	@Override
	public Sender getSender() {
		return sender;
	}

	@Override
	public <T> Topic<T> createTopic(String name, int port) {

		return new Topic<T>(name, port);
	}

	@Override
	public void onBytesReceived(byte[] buf) {

		String json = new String(buf);

		Gson gson = new Gson();
		EmptyMessage emess = null;
		try {
			emess = gson.fromJson(json, EmptyMessage.class);
		} catch (JsonParseException e) {
			System.out
					.println("MulticastParticipant: Parse exception, ignoring data.");
			return;
		}

		String topicName = emess.topicName;

		synchronized (this) {

			for (Subscriber<?> sub : subscribers) {
				if (sub.getTopic().getName().equals(topicName))
					sub.onNewData(json);
			}

		}

	}

	private <E> void createReceiver(Topic<E> topic) {
		try {
			MulticastSocket multicastSocket = MulticastSocketCreator
					.getMulticastSocket(basePort + topic.getPort());
			multicastSocket.setTimeToLive(DEFAULT_TTL);
			SocketAddress mcSocketAddress = new InetSocketAddress(address,
					basePort + topic.getPort());
			multicastSocket.joinGroup(mcSocketAddress, NetworkInterface
					.getByInetAddress(InetAddress.getByName("0.0.0.0")));
			Receiver receiver = new MulticastReceiver(multicastSocket);
			receivers.put(topic.getPort(), receiver);
			receiver.add(this);
			receiver.start();

		} catch (IOException e) {
			// TODO Add proper error handling
			e.printStackTrace();
		}
	}

}
