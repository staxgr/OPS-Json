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
import java.util.List;

import ops.data.Message;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class Subscriber<T extends Message> {
	private final Topic<T> topic;
	private final List<DataListener<T>> listeners = new ArrayList<DataListener<T>>();
	private final List<DeadlineListener> deadlineListeners = new ArrayList<DeadlineListener>();
	private final Participant participant;
	private final Gson gson = new Gson();
	private boolean printOnly;
	private volatile long deadlineTimeout;
	private Thread deadlineTimer;
	private Object dataMon = new Object();
	private volatile boolean newData;
	
	public enum OwnershipKind{SHARED, EXCLUSIVE, STICKY}
	OwnershipKind ownershipKind = OwnershipKind.SHARED;
	private volatile String currentStickyPublisher;
	private volatile T pendingData;
	private final Class<?> cls;

	public Subscriber(Topic<T> topic, Participant participant, Class<?> cls) {
		this.topic = topic;
		this.participant = participant;
		this.cls = cls;
		

	}

	public Topic<T> getTopic() {
		return topic;
	}

	public void addDataListener(DataListener<T> listener) {
		if (listeners.size() == 0) {
			participant.addSubscriber(this);
		}
		listeners.add(listener);
	}

	public void removeDeadlineListener(DeadlineListener listener) {
		deadlineListeners.remove(listener);
		if (deadlineListeners.size() == 0) {
			deadlineTimer.interrupt();
			deadlineTimer = null;
		}
	}
	
	public void addDeadlineListener(DeadlineListener listener) {
		if (deadlineListeners.size() == 0) {
			if(deadlineTimer == null) {
				deadlineTimer = new Thread(deadlineChecker);
				deadlineTimer.start();
			} 
		}
		deadlineListeners.add(listener);
	}

	public void removeDataListener(DataListener<T> listener) {
		listeners.remove(listener);
		if (listeners.size() == 0) {
			participant.removeSubsriber(this);
		}
	}

	@SuppressWarnings("unchecked")
	public void onNewData(String json) {
		if (printOnly) {
			System.out.println(json);
			return;
		}
		T data = null;
		try {
			data = (T) gson.fromJson(json, cls);
			if(!filter(data)) {
				return;
			}
		} catch (JsonParseException e) {
			System.out.println("Subscriber: Parse Exception, data will be ignored.");
			return;
		}
		newData = true;
		notifyNewData(data);
		synchronized (dataMon) {
			dataMon.notify();
		}
		
	}

	private void notifyNewData(T data) {
		for (DataListener<T> listener : listeners) {
			listener.onNewData(data);
		}
	}

	private boolean filter(T data) {
		
		switch (ownershipKind) {
		case SHARED:
			return true;
		case EXCLUSIVE:
			if(pendingData == null || pendingData.strength < data.strength) {
				pendingData = data;
			}
			return false;
			
		case STICKY:
			if(currentStickyPublisher == null || data.publisherName.equals(currentStickyPublisher)){
				if(currentStickyPublisher == null) {
					currentStickyPublisher = data.publisherName;
				}
				return true;
			} 
		}
		return false;
	}

	public void printOnly() {
		printOnly = true;

	}
	public long getDeadlineTimeout() {
		return deadlineTimeout;
	}
	public void setDeadlineTimeout(long deadlineTimeout) {
		this.deadlineTimeout = deadlineTimeout;
	}
	
	private synchronized void onDeadlineTimeout() {
		if(ownershipKind == OwnershipKind.EXCLUSIVE && pendingData != null) {
			notifyNewData(pendingData);		
		} else {
			currentStickyPublisher = null;
			for (DeadlineListener listener : deadlineListeners) {
				listener.onDeadlineMissed(this);
			}
		}
		pendingData = null;
	}
	
	Runnable deadlineChecker = new Runnable() {
		public boolean keepRunning = true;
		public void run() {
			while(keepRunning) {
				synchronized (dataMon) {
					try {
						newData = false;
						dataMon.wait(deadlineTimeout);
						if(!newData) {
							currentStickyPublisher = null;
							onDeadlineTimeout();			
						}
					} catch (InterruptedException e) {
						keepRunning = false;
					}
				}
				
			}
			
		}
	};
	
	public OwnershipKind getOwnershipKind() {
		return ownershipKind;
	}
	public void setOwnershipKind(OwnershipKind ownershipKind) {
		this.ownershipKind = ownershipKind;
	}

}
