package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "Search")
public class Search implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SearchID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long searchId;
	
//	@Column(name = "MakeModelID", nullable=false)
//	private long makeModelId;
	@ManyToOne
	@JoinColumn(name="MakeModelID")
	private MakeModel makeModel;
	
	@ManyToOne
	@JoinColumn(name = "LocationID")
	private Location location;

/*	@Column(name = "LocationID", nullable=false)
	private long locationId;*/

	@Column(name = "Radius", nullable=false)
	private long radius;

	public long getSearchId() {
		return searchId;
	}

	public void setSearchId(long searchId) {
		this.searchId = searchId;
	}

/*	public long getMakeModelId() {
		return makeModelId;
	}

	public void setMakeModelId(long makeModelId) {
		this.makeModelId = makeModelId;
	}*/
	
	public MakeModel getMakeModel() {
		return makeModel;
	}

	public void setMakeModel(MakeModel makeModel) {
		this.makeModel = makeModel;
	}

/*	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}*/

	public long getRadius() {
		return radius;
	}

	public void setRadius(long radius) {
		this.radius = radius;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
