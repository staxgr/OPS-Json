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
package ops.http;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


import com.google.gson.Gson;

@Path("/endpoint")
class Endpoint {
	
	private static Gson gson = new Gson();
	
	@PUT
	@Produces("text/html;charset=UTF-8")
	@Path("commit")
	public static String commit(@QueryParam("topic") String topicName, @QueryParam("message") String jsonData) {
		
		
		try {
//			Message message = gson.fromJson(jsonData, Message.class);
//			String dataType = message.dataType;
//			String key = message.key;
//			String topic = message.topicName;
			
			
		} catch (Exception e) {
			
			return gson.toJson(new ClientResponse(false, e.getMessage()));
			
		}
		
		return gson.toJson(new ClientResponse(true));
		
	}

}
