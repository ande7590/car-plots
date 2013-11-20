package com.carplots.persistence.carMeta.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity(name = "CarTrim")
public class CarTrim implements Serializable {

	@Id
	@Column(name="CarTrimID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long carTrimId;
	
	@Column(name="CarModelID")
	private long carModelId;
	
	@Column(name="Transmission")
	private String transmission;
	
	@Column(name="DriveTrain")
	private String driveTrain;
	
	@Column(name="TrimName")
	private String trimName;

	public long getCarTrimId() {
		return carTrimId;
	}

	public void setCarTrimId(long carTrimId) {
		this.carTrimId = carTrimId;
	}

	public String getTransmission() {
		return transmission;
	}

	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}

	public String getDriveTrain() {
		return driveTrain;
	}

	public void setDriveTrain(String driveTrain) {
		this.driveTrain = driveTrain;
	}

	public String getTrimName() {
		return trimName;
	}

	public void setTrimName(String trimName) {
		this.trimName = trimName;
	}

	public long getCarModelId() {
		return carModelId;
	}

	public void setCarModelId(long carModelId) {
		this.carModelId = carModelId;
	}
	
}
