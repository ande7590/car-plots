package com.carplots.scraper.dataimport.edmunds;

import java.lang.Exception;

public interface EdmundsRepository {

	public String getMakeData(String makeName) 
		throws EdmundsRepositoryFetchException;
	public String getMakeModelData(String makeName, String modelName)
		throws EdmundsRepositoryFetchException;
	
	static class EdmundsRepositoryFetchException extends Exception {};
}
