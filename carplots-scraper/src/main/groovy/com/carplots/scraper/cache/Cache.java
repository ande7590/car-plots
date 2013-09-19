package com.carplots.scraper.cache;

import java.io.Serializable;

public interface Cache<T extends Serializable> {	
	public T getCachedEntry(String entryName);	
	public void setCachedEntry(String entryName, T entry);	
}
