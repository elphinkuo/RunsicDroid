package com.runningmusic.dto;

import org.apache.http.HttpEntity;

import java.util.ArrayList;

public abstract class CirculateAction<Req, Resp> extends BaseAction<Req, Resp> {

	{
		type = ActionType.Circulate;
	}

	public abstract void setResponseObject(String source) throws Exception;

	public abstract void beforeCirculate(Object... object) throws Exception;

	public abstract void afterCirculate(Object... object) throws Exception;

	public abstract ArrayList<HttpEntity> getHttpEntityArray();

	public HttpEntity getHttpEntity() {
		return null;
	}

}
