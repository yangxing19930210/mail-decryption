package com.sekorm.entity;

import java.util.Date;

public class Log {
	
	private Integer id;
	private String sendName;
	private String addresseName;
	private String cCRName;
	private String subject;
	private String accessory ; 
	private String status;
	private String time;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getAddresseName() {
		return addresseName;
	}
	public void setAddresseName(String addresseName) {
		this.addresseName = addresseName;
	}
	public String getcCRName() {
		return cCRName;
	}
	public void setcCRName(String cCRName) {
		this.cCRName = cCRName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAccessory() {
		return accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
