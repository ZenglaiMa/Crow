package com.happier.crow.entities;

import java.util.Date;

public class Image {

	private int id;
	private int uploaderStatus;
	private int uploaderId;
	private String path;
	private Date uploadTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUploaderStatus() {
		return uploaderStatus;
	}

	public void setUploaderStatus(int uploaderStatus) {
		this.uploaderStatus = uploaderStatus;
	}

	public int getUploaderId() {
		return uploaderId;
	}

	public void setUploaderId(int uploaderId) {
		this.uploaderId = uploaderId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", uploaderStatus=" + uploaderStatus + ", uploaderId=" + uploaderId + ", path="
				+ path + ", uploadTime=" + uploadTime + "]";
	}

}
