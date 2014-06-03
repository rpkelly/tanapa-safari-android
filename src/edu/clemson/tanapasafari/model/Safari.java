package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Safari {
	
	private int id;
	private String name;
	private String description;
	private int headerMediaId;
	private int footerMediaId;
	private int tileMediaId;
	
	public Safari() {
		super();
	}
	
	public Safari(JSONObject jsonObject) {
		try {
			if (jsonObject.has("id")){
				this.setId(jsonObject.getInt("id"));
			}
			if (jsonObject.has("name")){
				this.setName(jsonObject.getString("name"));
			}
			if (jsonObject.has("description")) {
				this.setDescription(jsonObject.getString("description"));
			}
			if (jsonObject.has("header_media_id")){
				this.setHeaderMediaId(jsonObject.getInt("header_media_id"));
			}
			if (jsonObject.has("footer_media_id")) {
				this.setFooterMediaId(jsonObject.getInt("footer_media_id"));
			}
			if (jsonObject.has("tile_media_id")) {
				this.setTileMediaId(jsonObject.getInt("tile_media_id"));
			}		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getHeaderMediaId() {
		return headerMediaId;
	}
	public void setHeaderMediaId(int headerMediaId) {
		this.headerMediaId = headerMediaId;
	}
	public int getFooterMediaId() {
		return footerMediaId;
	}
	public void setFooterMediaId(int footerMediaId) {
		this.footerMediaId = footerMediaId;
	}
	public int getTileMediaId() {
		return tileMediaId;
	}
	public void setTileMediaId(int tileMediaId) {
		this.tileMediaId = tileMediaId;
	}

}
