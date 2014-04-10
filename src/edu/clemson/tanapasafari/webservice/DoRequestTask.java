package edu.clemson.tanapasafari.webservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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
		InputStream is = null;
		try {
			URL u = new URL((String)params.get("url"));
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod((String)params.get("method"));
			conn.setDoInput(true);
			if ((String)params.get("method") == "POST") {
				conn.setDoOutput(true);
				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write((String)params.get("data"));
				writer.flush();
				writer.close();
				os.close();
			}
			conn.connect();
			Response r = new Response();
			r.setResponseCode(conn.getResponseCode());
			is = conn.getInputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));;
			StringBuilder sb = new StringBuilder();
			String line;
			
			while ((line = br.readLine()) != null ) {
				sb.append(line);
			}
			
			r.setData(sb.toString());
			return r;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return null;
	}
}

