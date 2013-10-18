package com.carplots.scraper.dataimport.edmunds;

import java.lang.Exception;

public interface EdmundsRepository {
	
	public Object getMakeModelYearJSON(String makeName, 
			String modelName, String year) throws
			EdmundsRepositoryFetchException;
	
	public Object getMakeJSON(String makeName)
		throws EdmundsRepositoryFetchException ;
	
	static class EdmundsRepositoryFetchException extends Exception {};
}
