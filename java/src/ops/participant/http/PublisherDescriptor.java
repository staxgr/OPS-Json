package ops.participant.http;

public class PublisherDescriptor {

	private String topic;
	private String address;
	private int port;
	private ConnectionType connectionType = ConnectionType.UDP;

	public PublisherDescriptor() {
		
	}
	public PublisherDescriptor(String topic, String address, int port) {
		this.topic = topic;
		this.address = address;
		this.port = port;
		
	}
	
	public String getTopic() {
		return topic;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public ConnectionType getConnectionType() {
		return connectionType;
	}
	

}
