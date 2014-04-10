package edu.clemson.tanapasafari.webservice;

import java.util.HashMap;
import java.util.Map;

public class WebServiceClientHelper {
	
	@SuppressWarnings("unchecked")
	public static void doGet(String url, ResponseHandler responseHandler) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("method", "GET");
		params.put("responseHandler", responseHandler);
		new DoRequestTask().execute(params);
	}
	
	@SuppressWarnings("unchecked")
	public static void doPost(String url, String data, ResponseHandler responseHandler) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("method", "POST");
		params.put("responseHandler", responseHandler);
		params.put("data", data);
		new DoRequestTask().execute(params);
	}	

}
