package com.rabbahsoft.mobile.gpstracker;

public class GeoPosition {

	private long id;
	private String latLng;
	private String speed;
	private String degree;
	private String cellLocation;
	private String netOperator;
	private String netOperatorName;
	private String phoneId;
	private Long date;
	private String precision;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLatLng() {
		return latLng;
	}
	public void setLatLng(String latLng) {
		this.latLng = latLng;
	}
	public String getCellLocation() {
		return cellLocation;
	}
	public void setCellLocation(String cellLocation) {
		this.cellLocation = cellLocation;
	}
	public String getNetOperator() {
		return netOperator;
	}
	public void setNetOperator(String netOperator) {
		this.netOperator = netOperator;
	}
	public String getNetOperatorName() {
		return netOperatorName;
	}
	public void setNetOperatorName(String netOperatorName) {
		this.netOperatorName = netOperatorName;
	}
	public String getPhoneId() {
		return phoneId;
	}
	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
	
	
}
