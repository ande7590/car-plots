package com.carplots.scraper.dataimport.carsDotCom

import groovy.util.XmlSlurper;

import java.util.regex.Pattern
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;

class CarsDotComScraperHtmlImpl implements CarsDotComScraper {
	
	final static Logger logger = LoggerFactory.getLogger(CarsDotComScraperHtmlImpl.class)
	
	private static final ThreadLocal<XmlSlurper> xmlSlurper = new ThreadLocal<XmlSlurper>() {
		@Override
		protected XmlSlurper initialValue() {
			return new XmlSlurper(new SAXParserImpl())
		}
	}
	
	CarsDotComScraperHtmlImpl() {
		
	}
	
	Collection<Imported> getImported(CarsDotComCrawlerData crawlerData) {		
		def imported = []
		crawlerData.getPages().each { pageHtml ->			
			imported += doParse(pageHtml, crawlerData.search.searchId)
		}				
		return imported
	}
	
	private logIfNull(var, name, errors) {
		if (var == null) {
			logger.warn("${name} was not correctly parsed");
			errors.warning++
		}
	}
	
	private abortIfNull(var, name, errors) {
		if (var == null) {
			logger.warn("${name} was not correctly parsed");
			errors.errors++
			return true
		}
		return false
	}
	
	private XmlSlurper getSlurper() {
		return xmlSlurper.get()
	}
	
	private Collection<Imported> doParse(String pageHtml, long searchId) {
		
		def errors = [critical: 0, warning: 0]
		def imported = []
		
		logger.debug('parsing page html')
		def page = getSlurper().parseText(pageHtml)		
				
		def vehicles = findAllByClassName(page, { className ->			
			className ==~ /vehicle/
		})
		logger.debug("processing ${vehicles.size()} vehicles")
						
		vehicles.each { v ->						
			
			try {
				def initialWarning = errors.warning
				def listingIdMatch = v['@id'] =~ /\d+$/
				
				//if we can't parse listing, miles, price, or year, skip
				def listingId = (listingIdMatch)? 
					listingIdMatch[0] : null				
				if (abortIfNull(listingId, 'listingId', errors)) return
				
				def price = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?price.*?/
				}
				if (abortIfNull(price, 'price', errors)) return
																
				def miles = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?miles.*?/
				}
				if (abortIfNull(miles, 'miles', errors)) return					
				
				def year = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?modelYear.*?/
				}
				if (abortIfNull(year, 'year', errors)) return
						
				//these fields are nice to have, but not needed
				def color = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?color.*?/
				}			 
				logIfNull(color, 'color', errors)				
				
				def bodyStyle = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?bodyStyle.*?/
				}			
				logIfNull(bodyStyle, 'bodyStyle', errors)
				
				def carName = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?mmt.*?/
				}								
				logIfNull(carName, 'carName', errors)
					
				def engine = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?engine.*?/
				}			
				logIfNull(engine, 'engine', errors)
				
				def dealerName = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?sellerName.*?/
				}				
				logIfNull(dealerName, 'dealerName', errors)
								
				def dealerPhone = getFirst findAllByClassName(v) { className ->
					className ==~ /(?i).*?seller-phone.*?/
				}					
				logIfNull(dealerPhone, 'dealerPhone', errors)													
							
				//set required fields first					
				try {						
					def i = new Imported(
						listingId: listingId as Long,
						miles: cleanNumeric(miles.text()) as Integer,
						price: cleanNumeric(price.text()) as Integer,
						carYear: cleanNumeric(year.text()) as Integer,
						bodyStyle: cleanString(bodyStyle?.text()),
						carName: cleanString(carName?.text()),
						color: cleanString(color?.text()),
						dealerPhone: cleanNumeric(dealerPhone?.text()).replaceAll(/^0$/, ''),
						engine: cleanString(engine?.text()),
						sellerName: cleanString(dealerName?.text()),
						scraperRunId: CarsDotComScraperHtmlImplConfig.scraperRunId,
						errFlg: (errors.warning > initialWarning)? '1' : '0',
						sellerType: 'unknown',
						searchId: searchId
					)					
					imported << i
				}						
				catch (NumberFormatException ex) { 
					//don't worry about it
				}					
			} 
			catch (Throwable ex) {
				errors.critical++
				logger.error(ex.getMessage(), ex)
			}						
		}
		
		logger.debug("returning ${imported.size()} records")
		
		return imported
	}
	
	private def cleanNumeric(num) {
		if (num == null) return '0'		
		num.replaceAll(/\D/, '') //allow only digits
	}
	
	private def cleanString(str) {
		if (str == null) return ''
		//allow only alpha numeric (and space)
		str.replaceAll(/(?i)[^A-Z0-9 ]/, '').
		replaceAll(/\s+/, ' '). // with at most one space between words
		replaceAll(/^\s+/, ''). // no leading space
		replaceAll(/\s+$/, '')  // no trailing space
	}
	
	private def getFirst(collection) {
		return (collection?.size() > 0) ?
			collection[0] : null
	}
		
	private def findAllByClassName(def node, def matchFn) {
		node.depthFirst().findAll {
			(it['@class'] as String).split().grep {className ->
				matchFn(className)
			}?.size() > 0
		}
	}
	
	private def createTrySetDecorator(trySetObj, errorCallback) {			
		return { setterName, value ->
			try {				
				trySetObj[setterName](value)
			} 
			catch (Exception ex) {
				errorCallback(trySetObj, setterName, ex)				
			}
		}
	}
	
	//TODO: java conf
	static class CarsDotComScraperHtmlImplConfig {
		static final long scraperBatchId = 10
		static final long scraperRunId = -1
	}	
}
