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
