package ops.participant;

import java.util.ArrayList;

public abstract class Receiver {
	
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
