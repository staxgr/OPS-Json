package ops.participant.http;

import java.net.URL;

public class HttpDiscoveryService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		final URL warUrl = Discovery.class.getClassLoader().getResource("ops/participant/http/rest/");
        try {
        	WebServer.init();
            WebServer.addWebApp("/ops", warUrl.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
