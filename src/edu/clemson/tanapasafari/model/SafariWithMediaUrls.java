package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SafariWithMediaUrls extends SafariListItem {
	
	private String headerMediaType;
	private String headerMediaUrl;
	private String footerMediaType;
	private String footerMediaUrl;
	
	
	public SafariWithMediaUrls() {
		super();
	}
	
	public SafariWithMediaUrls(JSONObject jsonObject) {
		super(jsonObject);
		try {
			if (jsonObject.has("header_media_type")) {
				this.setHeaderMediaType(jsonObject.getString("header_media_type"));
			}
			if (jsonObject.has("header_media_url")) {
				this.setHeaderMediaUrl(jsonObject.getString("header_media_url"));
			}
			if (jsonObject.has("footer_media_type")) {
				this.setFooterMediaType(jsonObject.getString("footer_media_type"));
			}
			if (jsonObject.has("footer_media_url")) {
				this.setFooterMediaUrl(jsonObject.getString("footer_media_url"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHeaderMediaType() {
		return headerMediaType;
	}
	public void setHeaderMediaType(String headerMediaType) {
		this.headerMediaType = headerMediaType;
	}
	public String getHeaderMediaUrl() {
		return headerMediaUrl;
	}
	public void setHeaderMediaUrl(String headerMediaUrl) {
		this.headerMediaUrl = headerMediaUrl;
	}
	public String getFooterMediaType() {
		return footerMediaType;
	}
	public void setFooterMediaType(String footerMediaType) {
		this.footerMediaType = footerMediaType;
	}
	public String getFooterMediaUrl() {
		return footerMediaUrl;
	}
	public void setFooterMediaUrl(String footerMediaUrl) {
		this.footerMediaUrl = footerMediaUrl;
	}
	
}
