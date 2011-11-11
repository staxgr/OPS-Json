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

abstract class Receiver {
	
	protected ArrayList<ReceiverListener> listeners = new ArrayList<ReceiverListener>();

	public synchronized boolean add(ReceiverListener listener) {
		return listeners.add(listener);
	}

	public synchronized boolean remove(ReceiverListener listener) {
		return listeners.remove(listener);
	}
	
	public synchronized void notifyListeners(byte[] buf) {
		for (ReceiverListener listener : listeners) {
			listener.onBytesReceived(buf);
		}
	}
	
	public abstract void start();
	//public abstract void stop();

	public abstract boolean stop();
	
	

}
