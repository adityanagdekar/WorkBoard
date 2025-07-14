package com.project.workboard.dto;

public class ApiResponseDTO<T> {
    private boolean success;
    private T data;
    private String message;
    
	public ApiResponseDTO(boolean success, T data, String message) {
		super();
		this.success = success;
		this.data = data;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}

