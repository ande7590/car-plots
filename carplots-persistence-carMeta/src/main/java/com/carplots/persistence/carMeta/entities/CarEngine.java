package com.carplots.persistence.carMeta.entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity(name="CarEngine")
public class CarEngine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CarEngineID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long carEngineId;
	
	@Column(name="CarTrimID")
	private long carTrimId;
	
	@Column(name="DisplacementCC", nullable=false)
	private Integer displacementCC;
	
	@Column(name="Cylinders", nullable=false)
	private Integer cylinders;
	
	@Column(name="Description", nullable=false)
	private String description;

	public long getCarEngineId() {
		return carEngineId;
	}

	public void setCarEngineId(long carEngineId) {
		this.carEngineId = carEngineId;
	}

	public Integer getDisplacementCC() {
		return displacementCC;
	}

	public void setDisplacementCC(Integer displacementCC) {
		this.displacementCC = displacementCC;
	}

	public Integer getCylinders() {
		return cylinders;
	}

	public void setCylinders(Integer cylinders) {
		this.cylinders = cylinders;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCarTrimId() {
		return carTrimId;
	}

	public void setCarTrimId(long carTrimId) {
		this.carTrimId = carTrimId;
	}
}
