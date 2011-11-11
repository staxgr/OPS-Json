package ops.subscribe;

import ops.participant.Message;

public interface DataListener<T extends Message> {
	
	void onNewData(T data);
	
}
