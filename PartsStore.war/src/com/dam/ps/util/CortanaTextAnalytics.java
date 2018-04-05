package com.dam.ps.util;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONObject;

public class CortanaTextAnalytics {
	
	public static JSONObject getAnalytics(String textToAnalyze, String analyticsType){
		JSONObject requestBody = null;
		if(analyticsType.equals("sentiment")){
			requestBody = JSONObject.fromObject("{\"documents\": [{\"language\": \"en\",\"id\": \"1\",\"text\": \""+textToAnalyze+"\"}]}");
		}
		JSONObject result = new JSONObject();
		String urlString = "https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";
		String token= "574afb5d5a604992aad0a49a882d3c03";
		String method="POST";
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Ocp-Apim-Subscription-Key",  token);
			conn.setRequestProperty("Content-Type", "application/json"); 
			conn.getOutputStream().write(requestBody.toString().getBytes());
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			result = JSONObject.fromObject(IOUtils.toString(conn.getInputStream()));
			conn.disconnect();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public static void main(String[] args) {
		System.out.println(getAnalytics("I am Good","sentiment"));
	}
}

