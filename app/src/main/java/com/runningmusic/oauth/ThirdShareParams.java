package com.runningmusic.oauth;

public class ThirdShareParams {
	private int uid = 0;
	private String avatarUrl = null;
	private String title = null;
	private String text = null;
	private String urlString = null;
	private String imagePathString = null;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public String getImagePath() {
		return imagePathString;
	}

	public void setImagePath(String imagePathString) {
		this.imagePathString = imagePathString;
	}
}
