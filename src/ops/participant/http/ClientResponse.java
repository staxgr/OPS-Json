package ops.participant.http;

public class ClientResponse {
	private boolean ok;
	private String message;
	
	public ClientResponse() {
		// TODO Auto-generated constructor stub
	}
		
	public ClientResponse(boolean ok, String message) {
		super();
		this.ok = ok;
		this.message = message;
	}

	public ClientResponse(boolean ok) {
		super();
		this.ok = ok;
	}

	public boolean isOk() {
		return ok;
	}
	public String getMessage() {
		return message;
	}
}
