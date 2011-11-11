package ops.participant.http;

public class SubscriberDescriptor {

	private String topic;
	private String dataType;
	private String address;
	private int port;
	
	public SubscriberDescriptor() {
		
	}

	public SubscriberDescriptor(String topic, String address, int port, String dataType) {
		this.topic = topic;
		this.address = address;
		this.port = port;
		this.dataType = dataType;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}

}
