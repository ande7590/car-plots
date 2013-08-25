package com.carplots.scraper.dataimport.carsDotCom;

public interface CarsDotComRepository {

	public abstract String getSummaryPageHtml(def makeId, def modelId,
			def zipcode, def radius, def pageNum) throws CarsDotComRepositoryFetchException

	static class CarsDotComRepositoryFetchException extends Exception {}
}
