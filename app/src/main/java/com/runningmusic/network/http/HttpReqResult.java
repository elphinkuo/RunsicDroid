package com.runningmusic.network.http;


public class HttpReqResult {
	
	public enum ResultType {
		SUCCESS, FAILED
	}
	
	public HttpReqResult(ResultType result,String resultMsg){
		this.result=result;
		this.resultMsg=resultMsg;
	}
	
	private ResultType result;
	private String resultMsg;
	
	public ResultType getResult() {
		return result;
	}
	public void setResult(ResultType result) {
		this.result = result;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
}
