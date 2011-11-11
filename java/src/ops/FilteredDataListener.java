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
