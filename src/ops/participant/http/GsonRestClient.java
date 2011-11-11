package ops.participant.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

public class GsonRestClient {

	protected final Gson gson = new Gson();
	protected final String baseUrl;
	
	public GsonRestClient(String baseUrl) {
		this.baseUrl = baseUrl;
		
	}

	protected <D> D executeGet(String url, Type listType, Param... params) {
		try {
			String addParams = "?";
			for (Param param : params) {
				addParams += param.key + "=" + param.value + "&";
			}
			if(addParams.endsWith("&") || addParams.endsWith("?")) {
				addParams = addParams.substring(0, addParams.length() - 1);
			}
			
			
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(baseUrl + url + addParams);
			HttpResponse execute = client.execute(httpGet);
			return gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), listType);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
		
	}

	protected Param param(String key, String value) {
		return new Param(key, value);
	}
	
	protected class Param {
		String key;
		String value;
		public Param(String key, String value) {
			this.key = key;
			try {
				this.value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// Should never happen
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
	}

}
