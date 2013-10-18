package com.carplots.scraper.dataimport.edmunds;

import java.lang.Exception;

public interface EdmundsRepository {
	
	public Object getMakeModelYearJSON(String makeName, 
			String modelName, String year) throws
			EdmundsRepositoryFetchException;
	
	public Object getMakeJSON(String makeName)
		throws EdmundsRepositoryFetchException ;
	
	public static class EdmundsRepositoryFetchException extends Throwable {
		public EdmundsRepositoryFetchException(String msg, Throwable ex) {
			super(msg, ex);
		}			
	}
}
