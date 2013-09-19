package com.carplots.scraper.dataimport.carsDotCom

import java.util.regex.Matcher
import java.util.regex.Pattern
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import spock.lang.Specification;

class CarsDotComRepositoryTest extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CarsDotComRepositoryTest.class)
	
	static def MAKE_CHRYSLER = 20008
	static def MODEL_PT_CRUISER = 21744
	static def ZIPCODE_MPLS = 55423
	static def RADIUS_DEFAULT = 30
		
	def "test summaryPageHTML"() {
		def repo = new CarsDotComRepositoryImpl()
		when: "test a valid request that we would expect to return results"
		def requestHtml = repo.getSummaryPageHtml(
			MAKE_CHRYSLER, MODEL_PT_CRUISER, ZIPCODE_MPLS, RADIUS_DEFAULT, 0) as String
		logger.warn(requestHtml)		
		then:
		isSummaryPageCorrect(requestHtml)
	}
	
	static boolean isSummaryPageCorrect(String htmlData) {
		//groovy regex is acting weird, eclipse may be using a buggy version because
		//the groovyConsole works just fine when the same data and regex are used (the groovy version of the regex)
		Pattern regex = Pattern.compile("\\s+chrysler\\s+pt\\s+cruiser", Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
		Matcher regexMatcher = regex.matcher(htmlData)
		boolean result = regexMatcher.find()
		return result
	}	

}
