package edu.clemson.tanapasafari.model;

import java.util.Date;

public class Report {

	private long id;
	private long reportTypeId;
	private String content;
	private Date time;
	private double latitude;
	private double longitude;
	private long userId;
	private Media media;
	private boolean synch;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getReportTypeId() {
		return reportTypeId;
	}
	
	public void setReportTypeId(long reportTypeId) {
		this.reportTypeId = reportTypeId;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public boolean isSynch() {
		return synch;
	}

	public void setSynch(boolean synch) {
		this.synch = synch;
	}
	
	
	
}
