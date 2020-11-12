package utils;

import com.google.gson.Gson;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

//import org.springframework.http.MediaType;

public class HttpPostClient {


	public <T> T execute(String url, Object request, ResponseHandler<T> handler) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");

		if (request != null) {
			HttpEntity entity = createRequestHttpEntity2((String)request);
			httpPost.setEntity(entity);
		}
		
		T response = httpClient.execute(httpPost, handler);

		httpClient.close();
		return response;

	}

	private HttpEntity createRequestHttpEntity(Object pojo) {

	    Gson gson = new Gson();
		String json = gson.toJson(pojo);

//		System.out.println ("Request Data: " + json);
		StringEntity input = new StringEntity(json, Charsets.UTF_8);

		input.setContentType("application/json");
		input.setContentEncoding("utf-8");
		return input;
	}


	private HttpEntity createRequestHttpEntity2(String json) {


//		System.out.println ("Request Data: " + json);
		StringEntity input = new StringEntity(json, Charsets.UTF_8);

		input.setContentType("application/json");
		input.setContentEncoding("utf-8");
		return input;
	}

}
