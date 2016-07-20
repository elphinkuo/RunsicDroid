package com.runningmusic.dto;

import org.apache.http.HttpEntity;

public abstract class BaseAction<Req, Resp> {

	//请求类型normal正常 circulate循环
	public enum ActionType {
		Normal, Circulate
	}
	
	private Req req;
	private Resp resp;
	protected ActionType type;
	protected String requestUrl;
	
	{
		type=ActionType.Normal;
	}

	public abstract HttpEntity getHttpEntity();
	public abstract void setResponseObject(String source) throws Exception;

//	public int getResultCode() throws Exception{
//		return Integer.valueOf(getResp().toString());
//	}
	
	public String getStatus() throws Exception {
		return getResp().toString();
	}

//	protected HashMap<String, String> getSessionHashMap() {
//		HashMap<String, String> paramsMap=new HashMap<String, String>();
//		paramsMap.put("protocolVersion", "");
//		paramsMap.put("sessionId", "");
//		return paramsMap;
//	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public Req getReq() {
		return req;
	}

	public void setReq(Req req) {
		this.req = req;
	}

	public Resp getResp() {
		return resp;
	}

	public void setResp(Resp resp) {
		this.resp = resp;
	}
	public ActionType getType() {
		return type;
	}
	public void setType(ActionType type) {
		this.type = type;
	}
	
	


}
