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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


class MulticastReceiver extends Receiver implements Runnable {

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
