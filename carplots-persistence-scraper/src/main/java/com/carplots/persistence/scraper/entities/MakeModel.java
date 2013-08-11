package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "MakeModel")
public class MakeModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "MakeModelID", nullable=false)
	private long makeModelId;
	
	@Column(name = "MakeID", nullable=false)
	private long makeId;
	
	@Column(name = "ModelID", nullable=false)
	private long modelId;
	
	@Column(name = "MakeName", nullable=false, length=128)
	private String makeName;
	
	@Column(name = "modelName", nullable=false, length=128)
	private String modelName;

	public long getMakeModelId() {
		return makeModelId;
	}

	public void setMakeModelId(long makeModelId) {
		this.makeModelId = makeModelId;
	}

	public long getMakeId() {
		return makeId;
	}

	public void setMakeId(long makeId) {
		this.makeId = makeId;
	}

	public long getModelId() {
		return modelId;
	}

	public void setModelId(long modelId) {
		this.modelId = modelId;
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
	
}
