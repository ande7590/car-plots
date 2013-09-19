package com.carplots.scraper.dataimport

import groovyx.net.http.HTTPBuilder;

public abstract class AbstractHttpBuilderScraperRepository {
	
	protected AbstractHttpBuilderScraperRepository() {}
	
	
	private final Object threadLocalLock = new Object()
	
	protected abstract String getScraperBaseURL()
	protected abstract void doHandleFailure(def response);
	
	private volatile ThreadLocal<HTTPBuilder> httpBuilderThreadLocal
	protected HTTPBuilder getHttpBuilder() {
		if (httpBuilderThreadLocal == null) {
			synchronized (threadLocalLock) {
				if (httpBuilderThreadLocal == null) {
					httpBuilderThreadLocal = getHttpBuilderThreadLocal(getScraperBaseURL())
				}
			}
		}
		return httpBuilderThreadLocal.get()
	}
	
	protected def getHttpBuilderThreadLocal(final String scraperBaseURL) {
		return new ThreadLocal<HTTPBuilder>() {
			protected def initialValue() {
				def http = new HTTPBuilder(scraperBaseURL)
				http.handler.failure = { resp ->
					doHandleFailure(resp)
				}
				return http
			}
		}
	}
	
}
