package ops.participant.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import ops.participant.Receiver;

public class MulticastReceiver extends Receiver implements Runnable {

	private static final int DGRAM_MAX_SIZE = 60000;
	private static final int TIMEOUT = 500;
	private final MulticastSocket multicastSocket;
	private final Thread thread;
	private boolean keepRunning;
	byte[] buf = new byte[DGRAM_MAX_SIZE];
	DatagramPacket packet = new DatagramPacket(buf, buf.length);

	public MulticastReceiver(MulticastSocket multicastSocket) {
		this.multicastSocket = multicastSocket;
		try {
			multicastSocket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			// TODO Add proper logging
			e.printStackTrace();
		}
		thread = new Thread(this);

	}

	@Override
	public void start() {
		thread.start();

	}

	@Override
	public boolean stop() {
		keepRunning = false;
		multicastSocket.close();
		try {
			thread.join(TIMEOUT * 2);
			return !thread.isAlive();
		} catch (InterruptedException e) {
			return false;
		}
	}

	@Override
	public void run() {
		keepRunning = true;
		while (keepRunning) {
			try {
				multicastSocket.receive(packet);
				if (packet.getLength() > 0) {
					notifyListeners(buf);
				}

			} catch (SocketTimeoutException te) {
				if (!keepRunning) {
					break;
				}

			} catch (IOException e) {
				if (keepRunning) {
					e.printStackTrace();
				} else {
					//this exception was expected, socket was closed.
				}
			}
		}

	}

}
