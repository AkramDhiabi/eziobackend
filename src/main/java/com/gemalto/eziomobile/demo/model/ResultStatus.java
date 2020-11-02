package com.gemalto.eziomobile.demo.model;

import org.springframework.http.HttpStatus;

public class ResultStatus {

	private String message;
	private HttpStatus statusCode;
	private int responseCode;
	private Object templateObject;
	private Object tempObject;

	public ResultStatus() {
		super();
	}

	public ResultStatus(String message, HttpStatus statusCode) {
		super();
		this.message = message;
		this.statusCode = statusCode;
	}

	public ResultStatus(String message, HttpStatus statusCode, int responseCode) {
		super();
		this.message = message;
		this.statusCode = statusCode;
		this.responseCode = responseCode;
	}
	
	public ResultStatus(String message, HttpStatus statusCode, int responseCode, Object tempObject) {
		super();
		this.message = message;
		this.statusCode = statusCode;
		this.responseCode = responseCode;
		this.tempObject = tempObject;
	}

	public ResultStatus(HttpStatus statusCode, int responseCode, Object templateObject,
			Object tempObject) {
		super();
		this.statusCode = statusCode;
		this.responseCode = responseCode;
		this.templateObject = templateObject;
		this.tempObject = tempObject;
	}

	public ResultStatus(HttpStatus statusCode, int responseCode, Object templateObject) {
		super();
		this.statusCode = statusCode;
		this.responseCode = responseCode;
		this.templateObject = templateObject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public Object getTemplateObject() {
		return templateObject;
	}

	public void setTemplateObject(Object templateObject) {
		this.templateObject = templateObject;
	}

	public Object getTempObject() {
		return tempObject;
	}

	public void setTempObject(Object tempObject) {
		this.tempObject = tempObject;
	}

	@Override
	public String toString() {
		return "ResultStatus [message=" + message + ", statusCode=" + statusCode + ", responseCode=" + responseCode
				+ ", templateObject=" + templateObject + ", tempObject=" + tempObject + "]";
	}

}
