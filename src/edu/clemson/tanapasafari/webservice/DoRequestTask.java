package edu.clemson.tanapasafari.webservice;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntityHC4;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

class DoRequestTask extends AsyncTask<Map<String, Object>, Void, Response> {

	private ResponseHandler responseHandler;
	
	@Override
	protected Response doInBackground(Map<String, Object>... params) {
		responseHandler = (ResponseHandler) params[0].get("responseHandler");
		return doRequest(params[0]);
	}

	@Override
	protected void onPostExecute(Response result) {
		responseHandler.onResponse(result);
	}
	
	private static Response doRequest(Map<String, Object> params) {
		//InputStream is = null;
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpUriRequest request = null;
			if ("POST".equals(params.get("method"))) {
				HttpPost post = new HttpPost((String)params.get("url"));
				/*
				// If the data param is a map, it is multipart.
				if (params.get("data") instanceof Map){
					
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) params.get("data");
					
					MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
					entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					for (String key : data.keySet()) {
						Object value = data.get(key);
						if ( value instanceof String ) {
							entityBuilder.addTextBody(key, (String) value);
							entityBuilder.
						} else if (value instanceof File){
							entityBuilder.addBinaryBody(key, (File)value);
						}
					}
					
					
					post.setEntity(entityBuilder.build());
		            
				} else if (params.get("data") instanceof String){
					post.setEntity(new StringEntity((String)params.get("data")));
				}
				*/
				
				Object data = params.get("data");
				
				if (data instanceof String) {
					post.setEntity(new StringEntity((String)params.get("data")));
				} else if (data instanceof File) {
					String mimeTypeString = (String) params.get("mimeType");
					ContentType mimeTypeContentType = ContentType.create(mimeTypeString);
					File dataFile = (File) data;
					FileEntityHC4 entity = new FileEntityHC4(dataFile, mimeTypeContentType);
					post.setEntity(entity);
				}
				
				request = post;
			} else if ("GET".equals(params.get("method"))) {
				HttpGet get = new HttpGet((String)params.get("url"));
				request = get;
			}
			
			HttpResponse httpResponse = client.execute(request);
			Response response = new Response();
			response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
			response.setData(EntityUtils.toString(httpResponse.getEntity()));
			return response;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

