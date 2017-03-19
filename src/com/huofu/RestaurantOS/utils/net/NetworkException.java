package com.huofu.RestaurantOS.utils.net;

public class NetworkException extends Exception {
	
	private static final long serialVersionUID = 475022994858770424L;

	private int statusCode = -1;

	public NetworkException(String msg) {
		super(msg);
	}

	public NetworkException(Exception cause) {
		super(cause);
	}

	public NetworkException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public NetworkException(String msg, Exception cause) {
		super(msg, cause);
	}

	public NetworkException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public NetworkException() {
		super();
	}

	public NetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NetworkException(Throwable throwable) {
		super(throwable);
	}

	public NetworkException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
