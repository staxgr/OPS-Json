package ops.participant.http;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import ops.participant.Message;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

@Path("/endpoint")
public class Endpoint {
	
	private static Gson gson = new Gson();
	
	@PUT
	@Produces("text/html;charset=UTF-8")
	@Path("commit")
	public static String commit(@QueryParam("topic") String topicName, @QueryParam("message") String jsonData) {
		
		
		try {
			Message message = gson.fromJson(jsonData, Message.class);
			String dataType = message.dataType;
			String key = message.key;
			String topic = message.topicName;
			
			
		} catch (Exception e) {
			
			return gson.toJson(new ClientResponse(false, e.getMessage()));
			
		}
		
		return gson.toJson(new ClientResponse(true));
		
	}

}
