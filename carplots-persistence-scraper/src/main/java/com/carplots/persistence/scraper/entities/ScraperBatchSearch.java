package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "ScraperBatchSearch")
public class ScraperBatchSearch implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ScraperBatchSearchID", nullable=false)
	private long scraperBatchSearchId;
	
	@ManyToOne(optional=false, targetEntity=ScraperBatch.class)
	@JoinColumn(name="ScraperBatchID", referencedColumnName="ScraperBatchID")
	private ScraperBatch scraperBatch;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="SearchID")
	private Search search;

	public ScraperBatch getScraperBatch() {
		return scraperBatch;
	}

	public void setScraperBatch(ScraperBatch scraperBatch) {
		this.scraperBatch = scraperBatch;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}
}
