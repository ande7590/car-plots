package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "ScraperBatch")
public class ScraperBatch implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ScraperBatchID", nullable=false)
	private long scraperBatchId;
	
	@Column(name = "ScraperBatchDesc", nullable=false, length=255)
	private String scraperBatchDesc;
}
