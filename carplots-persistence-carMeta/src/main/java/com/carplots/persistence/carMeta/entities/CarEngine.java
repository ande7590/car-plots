package com.carplots.persistence.carMeta.entities;

import javax.persistence.*;

@Entity(name="CarEngine")
public class CarEngine {

	@Id
	@Column(name="CarEngineID")
	private long carEngineId;
	
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
}
