package com.carplots.persistence.carMeta.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;


@Entity(name = "CarModel")
public class CarModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CarModelID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long carModelId;
	
	@Column(name="MakeModelID", nullable=true)
	private Long makeModelId;
	
	@Column(name="MakeName", nullable=false)
	private String makeName;
	
	@Column(name="ModelName", nullable=false)
	private String modelName;
	
	@Column(name="Year", nullable=false)
	private Integer year;
	
	@Column(name="ModelType", nullable=false)
	private String modelType;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="CarModelID", referencedColumnName="CarModelID")
	private List<CarTrim> trims;

	public long getCarModelId() {
		return carModelId;
	}

	public void setCarModelId(long carModelId) {
		this.carModelId = carModelId;
	}

	public String getMakeName() {
		return makeName;
	}

	public void setMakeName(String makeName) {
		this.makeName = makeName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<CarTrim> getTrims() {
		return trims;
	}

	public void setTrims(List<CarTrim> trims) {
		this.trims = trims;
	}

	public Long getMakeModelId() {
		return makeModelId;
	}

	public void setMakeModelId(Long makeModelId) {
		this.makeModelId = makeModelId;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}	
}
