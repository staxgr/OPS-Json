package ops.subscribe;

import ops.participant.Message;

public interface Filter {
	
	public boolean filter(Message message);

}
