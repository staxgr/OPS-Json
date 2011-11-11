package ops.subscribe;

import ops.participant.Message;

public class FilteredDataListener<E extends Message> implements DataListener<E>{

	protected final Filter[] filters;
	private final DataListener<E> listener;
	
	public FilteredDataListener(DataListener<E> listener, Filter... filters) {
		this.listener = listener;
		this.filters = filters;
		
		
	}
	@Override
	public final void onNewData(E data) {
		for (Filter filter : filters) {
			if(!filter.filter(data)) {
				return;
			}
		}
		listener.onNewData(data);		
		
	}

}
