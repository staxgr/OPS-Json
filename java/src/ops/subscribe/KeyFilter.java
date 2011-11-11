package ops.subscribe;

import ops.participant.Message;

public class KeyFilter implements Filter{
	
	private String key;
	
	public KeyFilter(String key) {
		this.key = key;

	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean filter(Message message) {

		if(message.key == null && key != null) {
			return false;
		}
		if(message.key == null && key == null) {
			return true;
		}
		return message.key.equals(key);
	}

}
