package com.carplots.persistence.scraper.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.swing.text.DateFormatter;

@Entity(name = "ScraperRun")
public class ScraperRun implements Serializable {

	private static DateFormat dateStringFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ScraperRunID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long scraperRunId;
	
	@Column(name = "ScraperRunDT", nullable=false)
	private Date scraperRunDt;
	
	@Column(name = "RunCompleted", nullable = false)
	private boolean runCompleted;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="ScraperBatchID")
	private ScraperBatch scraperBatch;

	public ScraperBatch getScraperBatch() {
		return scraperBatch;
	}

	public void setScraperBatch(ScraperBatch scraperBatch) {
		this.scraperBatch = scraperBatch;
	}

	public long getScraperRunId() {
		return scraperRunId;
	}

	public void setScraperRunId(long scraperRunId) {
		this.scraperRunId = scraperRunId;
	}

	public Date getScraperRunDt() {
		return scraperRunDt;
	}

	public void setScraperRunDt(Date scraperRunDt) {
		this.scraperRunDt = scraperRunDt;
	}

	public boolean isRunCompleted() {
		return runCompleted;
	}

	public void setRunCompleted(boolean runCompleted) {
		this.runCompleted = runCompleted;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getScraperRunDateString() {
		return dateStringFormatter.format(scraperRunDt);
	}
	
}
