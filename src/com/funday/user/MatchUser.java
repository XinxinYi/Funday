package com.funday.user;

public class MatchUser {
	private String openid;
	private String message;
	private String toOpenid;
	private boolean ifMatch;
	private boolean inMatch;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getToOpenid() {
		return toOpenid;
	}
	public void setToOpenid(String toOpenid) {
		this.toOpenid = toOpenid;
	}
	public boolean isIfMatch() {
		return ifMatch;
	}
	public void setIfMatch(boolean ifMatch) {
		this.ifMatch = ifMatch;
	}
	public boolean isInMatch() {
		return inMatch;
	}
	public void setInMatch(boolean inMatch) {
		this.inMatch = inMatch;
	}

}
