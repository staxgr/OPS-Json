package ops.subscribe;

public interface DeadlineListener {
	
	void onDeadlineMissed(Subscriber<?> subscriber);

}
