package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="Location")
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "LocationID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long locationId;
	
	@Column(name = "Zipcode", nullable=false, length=5)
	private String zipcode;
	
	@Column(name = "City", nullable=false, length=255)
	private String city;
	
	@Column(name = "State", nullable=false, length=2)
	private String state;
	
	@Column(name = "Latitude", nullable=false)
	private double latitude;
	
	@Column(name = "Longitude", nullable=false)
	private double longitude;

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
