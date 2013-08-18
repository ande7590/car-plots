package com.carplots.persistence.scraper.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name = "Imported")
public class Imported implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ImportedID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long importedId;
	
	@Column(name = "Engine", length=32, nullable=false)
	private String engine;
	
	@Column(name = "DealerPhone", length=16, nullable=false)
	private String dealerPhone;
	
	@Column(name = "CarYear", nullable=false)
	private Integer carYear;
	
	@Column(name = "ErrFlg", length=32, nullable=false)
	private String errFlg;
	
	@Column(name = "Color", length=64, nullable=false)
	private String color;
	
	@Column(name = "Price", nullable=false)
	private Integer price;
	
	@Column(name = "BodyStyle", nullable=false, length=32)
	private String bodyStyle;
	
	@Column(name = "SellerType", nullable=false, length=32)
	private String sellerType;
	
	@Column(name = "ListingID", nullable=false)
	private Long listingId;
	
	@Column(name = "Miles", nullable=false)
	private Integer miles;
	
	@Column(name = "CarName", nullable=false)
	private String carName;

	@Column(name = "CarSearchID")
	private Long searchId;
	
	@Column(name = "ScraperRunID")
	private Long scraperRunId;
	
	@Column(name = "SellerName")
	private String sellerName;

	public Long getScraperRunId() {
		return scraperRunId;
	}

	public void setScraperRunId(Long scraperRunId) {
		this.scraperRunId = scraperRunId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public Long getSearchId() {
		return searchId;
	}

	public void setSearchId(Long searchId) {
		this.searchId = searchId;
	}

	public long getImportedId() {
		return importedId;
	}

	public void setImportedId(long importedId) {
		this.importedId = importedId;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getDealerPhone() {
		return dealerPhone;
	}

	public void setDealerPhone(String dealerPhone) {
		this.dealerPhone = dealerPhone;
	}

	public Integer getCarYear() {
		return carYear;
	}

	public void setCarYear(Integer carYear) {
		this.carYear = carYear;
	}

	public String getErrFlg() {
		return errFlg;
	}

	public void setErrFlg(String errFlg) {
		this.errFlg = errFlg;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getBodyStyle() {
		return bodyStyle;
	}

	public void setBodyStyle(String bodyStyle) {
		this.bodyStyle = bodyStyle;
	}

	public String getSellerType() {
		return sellerType;
	}

	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}

	public Long getListingId() {
		return listingId;
	}

	public void setListingId(Long listingId) {
		this.listingId = listingId;
	}

	public Integer getMiles() {
		return miles;
	}

	public void setMiles(Integer miles) {
		this.miles = miles;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}
}
