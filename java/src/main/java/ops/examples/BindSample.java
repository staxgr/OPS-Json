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
package ops.examples;

import ops.bind.OpsBinder;
import ops.bind.Participate;
import ops.bind.Subscribe;

@Participate (domain="ops.mc://235.4.3.2:12000")
public class BindSample {
	
	@Subscribe (topic = "TestTopic")
	public void onData(SomeData someData){
		System.out.println("AutoBind.onData() " + someData.i);
	}
	
	@Participate (domain="ops.mc://235.4.3.2:12000")
	public static class Inner {
		@Subscribe (topic = "TestTopic")
		public void incomming(SomeData data) {
			System.out.println("BindSample.Inner.incomming() " + data.i);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		BindSample sample = new BindSample();
		Inner inner = new BindSample.Inner();
		OpsBinder.bind(sample);
		OpsBinder.bind(inner);
		
		synchronized (sample) {
			sample.wait();
		}	
	}
	

}
