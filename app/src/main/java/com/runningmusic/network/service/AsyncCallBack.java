package com.runningmusic.network.service;

import com.runningmusic.dto.BaseAction;

public interface AsyncCallBack {

	@SuppressWarnings("rawtypes")
	public void onCallBack(final BaseAction action);

}
