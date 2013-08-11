package com.carplots.persistence.dao.hibernate;

import java.util.Iterator;

import org.hibernate.ScrollableResults;

/**
 * 
 * @author https://forum.hibernate.org/viewtopic.php?f=1&t=991911
 *
 * @param <ENTITY_TYPE> the entity to scroll over
 */
public class ScrollableResultsIterator<ENTITY_TYPE> implements Iterator<ENTITY_TYPE> {

	private ScrollableResults scrollableResult = null;
	private boolean isFirst;	
	private boolean isEmpty;
	private boolean skipFetch; 
	private ENTITY_TYPE firstEntity;
	
	public ScrollableResultsIterator(ScrollableResults scrollableResult) {
		this.scrollableResult = scrollableResult;	
		isFirst = true;
		isEmpty = false;
		firstEntity = null;
		skipFetch = false;
	}
	
	@Override
	public boolean hasNext() {
		
		//isLast() doesn't handle the case of an empty result set,
		//check if we can call next(), but only perform this ONCE (see Java documentation
		//for how hasNext() should behave), so multiple calls to hasNext() are safe.
		if (isFirst && !isEmpty) {		
			//peek ahead (only once)
			boolean hasResults = scrollableResult.next();
			//tell next() not to advance iterator
			skipFetch = true;
			//check if first call to next() was false (this means the results are empty)
			if (!hasResults) {
				isEmpty = true;
			}				
			//never do this again for the lifetime of this object
			isFirst = false;
		}
		
		return !isEmpty && !scrollableResult.isLast();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ENTITY_TYPE next() {
								
		if (skipFetch) {
			//reset the flag since we only want to skip once (see hasNext())
			skipFetch = false;
		} 
		else {			
			scrollableResult.next();
		}
						
	    return ((ENTITY_TYPE) scrollableResult.get()[0]);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");      
	}
	
}
