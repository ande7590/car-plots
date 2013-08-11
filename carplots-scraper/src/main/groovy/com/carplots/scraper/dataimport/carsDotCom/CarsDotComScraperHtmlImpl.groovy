package com.carplots.scraper.dataimport.carsDotCom

import java.util.regex.Pattern
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;

class CarsDotComScraperHtmlImpl implements CarsDotComScraper {
	
	XmlSlurper xmlSlurper;
	
	CarsDotComScraperHtmlImpl() {
		this.xmlSlurper = new XmlSlurper(new SAXParserImpl())	
	}
	
	Collection<Imported> getImported(CarsDotComCrawlerData crawlerData) {		
		def imported = []
		crawlerData.getPages().each { pageHtml ->
			imported.add(doParse(pageHtml))	
		}				
		return imported
	}
	
	private Imported doParse(String pageHtml) {
		
		def page = xmlSlurper.parseText(pageHtml)
		def vehicles = findAllByClassName(page, { className ->			
			className ==~ /.*?vehicle-info.*?/
		})
		vehicles.each { v ->						
			def engine
			def dealerPhone
			def year = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}						
			def color = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?color.*?/
			}			 
			def price = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def bodyStyle = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def carName = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def sellerType = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def listingId = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def miles = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}			
			def scraperRunId = getFirst findAllByClassName(v) { className ->
				className ==~ /(?i).*?modelYear.*?/
			}						
		}
		
		return null
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
	
	
}
