/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops.publish;

import com.google.gson.Gson;

import ops.Topic;
import ops.participant.Message;
import ops.participant.Participant;

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
