package ops.subscribe;

import ops.participant.Message;

public class KeyFilteredDataListener<E extends Message> extends FilteredDataListener<E>{

	private KeyFilter filter;

	public KeyFilteredDataListener(String key, DataListener<E> listener) {
		super(listener, new KeyFilter(key));
		filter = (KeyFilter) filters[0];	
	}
	
	public KeyFilter getFilter() {
		return filter;
	}
		
	

}
