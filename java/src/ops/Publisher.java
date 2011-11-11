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

import ops.data.Message;

import com.google.gson.Gson;


/**
 * 
 * @author Anton
 */
public class Publisher<T extends Message> {
	private final Topic<T> topic;
	private final Participant participant;
	private final Gson gson = new Gson();
	private String name = "no_name";

	public Publisher(Topic<T> topic, Participant part) {
		this.topic = topic;
		this.participant = part;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	

	public void write(T data) {

		data.publisherName = name;
		data.dataType = data.getClass().getName();
		data.topicName = topic.getName();
		
		participant.getSender().sendTo(gson.toJson(data).getBytes(),topic);

	}

	public void writeRaw(String string) {
		participant.getSender().sendTo(string.getBytes(),topic);
		
	}
	
}
