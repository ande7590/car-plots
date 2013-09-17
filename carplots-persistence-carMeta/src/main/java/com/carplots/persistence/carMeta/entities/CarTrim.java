package com.carplots.persistence.carMeta.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity(name = "CarTrim")
public class CarTrim implements Serializable {

	@Id
	@Column(name="CarTrimID")
	private long carTrimId;
	
//	@Column(name="CarModelI")
//	private Integer carModelId;
	
//	@
//	private String engine;
//	
	@Column(name="Transmission")
	private String transmission;
	
	@Column(name="DriveTrain")
	private String driveTrain;
	
	@Column(name="TrimName")
	private String trimName;
	
}
