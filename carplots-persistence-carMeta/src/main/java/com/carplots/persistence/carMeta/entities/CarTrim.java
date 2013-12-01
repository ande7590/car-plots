package com.carplots.persistence.carMeta.entities;

import java.io.Serializable;
import java.util.Collection;

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
	
	@Column(name="MpgHighway")
	private Integer mpgHighway;
	
	@Column(name="MpgCity")
	private Integer mpgCity;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable( name="CarTrimEngine",
				joinColumns={@JoinColumn(name="CarTrimID", referencedColumnName="CarTrimID")},
				inverseJoinColumns={@JoinColumn(name="CarEngineID", referencedColumnName="CarEngineID")})
	private Collection<CarEngine> engines;

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

	public Integer getMpgHighway() {
		return mpgHighway;
	}

	public void setMpgHighway(Integer mpgHighway) {
		this.mpgHighway = mpgHighway;
	}

	public Integer getMpgCity() {
		return mpgCity;
	}

	public void setMpgCity(Integer mpgCity) {
		this.mpgCity = mpgCity;
	}

	public Collection<CarEngine> getEngines() {
		return engines;
	}

	public void setEngines(Collection<CarEngine> engines) {
		this.engines = engines;
	}
	
}
