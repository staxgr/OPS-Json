package ops.bind;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Participate {
	String domain();

}
