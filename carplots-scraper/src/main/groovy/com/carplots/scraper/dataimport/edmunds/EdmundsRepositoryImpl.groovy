package com.carplots.scraper.dataimport.edmunds

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonSlurper;
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method.*

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.carplots.scraper.ScraperConfigService
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException

import com.google.inject.Inject

/*
 * The majority of this is sort of throw-away since this
 * import should only happen once every couple years or so.
 */
class EdmundsRepositoryImpl
	implements EdmundsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundsRepositoryImpl.class)
	
	public static String[] edmundsMakes = [
		"acura", "alfa-romeo", "am-general", "aston-martin", "audi",
		"bentley", "bmw", "bugatti", "buick", "cadillac", "chevrolet",
		"chrysler", "daewoo", "dodge", "eagle", "ferrari", "fiat",
		"fisker", "ford", "geo", "gmc", "honda", "hummer", "hyundai",
		"infiniti", "isuzu", "jaguar", "jeep", "kia", "lamborghini",
		"land-rover", "lexus", "lincoln", "lotus", "maserati", "maybach",
		"mazda", "mclaren", "mercedes-benz", "mercury", "mini",
		"mitsubishi", "nissan", "oldsmobile", "panoz", "plymouth",
		"pontiac", "porsche", "ram", "rolls-royce", "saab", "saturn",
		"scion", "smart", "spyker", "subaru", "suzuki", "tesla",
		"toyota", "volkswagen", "volvo"
	]
	
	public static String[] edmundsMakesJSON = [
		"Acura", "Alfa Romeo", "AM General", "Aston Martin", "Audi", "Bentley",
		"BMW", "Bugatti", "Buick", "Cadillac", "Chevrolet", "Chrysler",
		"Daewoo", "Dodge", "Eagle", "Ferrari", "FIAT", "Fisker",
		"Ford", "Geo", "GMC", "Honda", "HUMMER", "Hyundai",
		"Infiniti", "Isuzu", "Jaguar", "Jeep", "Kia", "Lamborghini",
		"Land Rover", "Lexus", "Lincoln", "Lotus", "Maserati", "Maybach",
		"Mazda", "McLaren", "Mercedes-Benz", "Mercury", "MINI", "Mitsubishi",
		"Nissan", "Oldsmobile", "Panoz", "Plymouth", "Pontiac", "Porsche",
		"Ram", "Rolls-Royce", "Saab", "Saturn", "Scion", "smart",
		"Spyker", "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen",
		"Volvo"]
	
	@Inject
	EdmundsRepositoryConfiguration repoConfig
	
	public String[] getMakes() {
		return edmundsMakes.clone();
	}
			
	@Override
	public Object getMakeJSON(String makeName) {
		
		if (!edmundsMakesJSON.find { it.equals(makeName)} ) {
			throw new IllegalArgumentException("bad makeName argument: ${makeName}")
		}
		
		HTTPBuilder http = new HTTPBuilder(repoConfig.getMakeJSONSearchURL() + java.net.URLEncoder.encode(makeName))
		http.handler.failure = { resp ->
			doHandleFailure(resp)
		}
		
		def jsonResult = null
		def retries = 0
		while (jsonResult == null && retries < repoConfig.getNumRetries()) {
			jsonResult = http.get(contentType: JSON)
			if (jsonResult == null) {
				logger.warn('Edmunds JSON request returned nothing, sleeping...')
				try {
					Thread.sleep(repoConfig.getFailureSleepMS())
				} catch (Exception ex) {}
				retries++
			}
		}
		
		if (jsonResult == null) {
			logger.error('Unable to retrieve HTTP data, crawler aborting.')
			throw new EdmundsRepositoryFetchException()
		}
				
		return jsonResult
	}
	
	private final static def maxTries = 3
	@Override		
	public Object getMakeModelYearJSON(String makeName, String modelName,
			String year) {
			
			def numTries = 0
			def retVal = null
			
			//scraping pages is hap-hazard due to our reliance on FireFox, 
			//and the tons of Edmunds scripts that need to run in order to 
			//fully load a page.  If we fail attempt to do it again but wait a
			//little long (and scroll down farther).
			while (numTries < maxTries && retVal == null) {
				retVal = doGetMakeModelYearJSON(makeName, modelName,
					year, 30 + 20*numTries)
				numTries++
			}
			
			if (retVal == null) {
				logger.warn("error fetching ${makeName}, ${modelName}, ${year} ")
			}
			
			return retVal
	}
	
	private Object doGetMakeModelYearJSON(String makeName, String modelName,
			String year, numTimesToScrollAndWait) {
		
		//edmunds lazy loads the page as you scroll down (via JS), so
		//we cannot issue a simple HTTP request, we need to do a scrape
		//using a full-blown web browser
		WebDriver webDriver = getWebDriver()
		JsonSlurper jsSlurper = new JsonSlurper()
		
		//"style" is an edmunds key corresponding to a make, model, year, trim, and engine.
		//We'll need to fetch them so we can query all the data for this car.  Assume NULL
		//initially since we don't know what the style is until the page loads
		def stylesToProcess = [null]					
		def stylesCollectedDataMap = [:]
		
		try {
			while (stylesToProcess.size() > 0) {
				
				def style = stylesToProcess.pop()
				def queryURL = [
					repoConfig.getScraperBaseURL(),
					makeName.toLowerCase(),
					modelName.toLowerCase(),
					year].join('/')
				
				if (style != null) {
					queryURL += "?style=${style}"
				}
				webDriver.get(queryURL)							
				
				JavascriptExecutor js = (JavascriptExecutor) webDriver
				
				//scroll to bottom of page to get the entire page to load (hopefully)
				for (int second = 0;; second++) {
					if(second >= numTimesToScrollAndWait){
						break;
					}
					js.executeScript("window.scrollBy(0,300)", "");
					Thread.sleep(100);
				}
				
				try {
					//check if entire page is loaded (not fool proof)
					(new WebDriverWait(webDriver, 10)).until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver d) {
							String pageSource = d.getPageSource()
							return pageSource.contains('specs-diff') &&
								pageSource.contains("</body>") &&
								pageSource.contains("specs-pod")
						};
					})
				} catch (Exception ex) {
					logger.warn("error, skipping ${makeName}, ${modelName}, ${year} ", ex)
					continue
				}
				
				
				// if this is the first run, fetch all the possible "style" keys (car options packages)
				// from the dropdown
				if (style == null) {									
				
					def stylesListJSON = null
					try {
						//use JS to get the styles from the dropdown
						stylesListJSON = js.executeScript('''							
							var selectCandidates = document.getElementsByTagName("select")
							var specsSelectOptions = []
							var selectedStyleId = 0
							for(var i=0; i<selectCandidates.length; i++) {
							    var sel = selectCandidates[i]
							    if (sel.parentNode && sel.parentNode.id  && sel.parentNode.id.indexOf('specs-diff') >= 0) {
							        specsSelectOptions = sel.options
									selectedStyleId = sel.value
							    }
							}
							var results = new Object()
							for (var i=0;i<specsSelectOptions.length;i++) {
								
							    var opt = specsSelectOptions[i]
							    var text = opt.text
							    var value = opt.value
							    results[value] = text
							}
							

							return JSON.stringify({ selectedStyleId: selectedStyleId, results: results })																																
						''');
					} catch (Exception ex) {
						logger.warn("error fetching ${makeName}, ${modelName}, ${year} ", ex)
						return null
					}
																
					if (stylesListJSON == null) {
						throw new EdmundsRepositoryFetchException("Can't parse selection objects: ", null)
					}
					
					//setup the subsequent searches we will need for this car to get the other styles				
					def stylesListData = jsSlurper.parseText(stylesListJSON)
					
					//determine what this style is
					style = stylesListData["selectedStyleId"]
					
					if (style == 0) {
						logger.warn("error fetching ${makeName}, ${modelName}, ${year} ")
						return null
					}
										
					for (String key : stylesListData["results"].keySet()) {
						stylesToProcess << key
						stylesCollectedDataMap[key] = [
							desc: stylesListData[key]
						]
					}
					
				
				} 
				
				def styleDataJSON = null
				try {
					//parse the HTML data table to get engine size, trim, mpg, car options, etc
					styleDataJSON = js.executeScript('''
						var dataTableCandidates = document.getElementById("specs-pod").getElementsByTagName("div")
						var dataTables = []
						for (var i=0; i<dataTableCandidates.length; i++) {
							var elem = dataTableCandidates[i]
							if (elem.className.indexOf("data-table") >= 0) {
								dataTables.push(elem)
							}
						}
						var data = {}
						for (var i=0; i<dataTables.length; i++) {
							var dt = dataTables[0];
							var items = dt.getElementsByTagName('li')
							for (var j=0; j<items.length; j++) {
								var itm = items[j]
								var attrName = itm.getElementsByTagName('span')[0].innerHTML
								var attrVal = itm.getElementsByTagName('em')[0].innerHTML
								data[attrName] = attrVal
							}
						}
						return JSON.stringify(data)
					''')			
				} catch (Exception ex) {
					logger.warn("error, skipping ${makeName}, ${modelName}, ${year} ", ex)
					continue
				}
									
				def styleAllData = jsSlurper.parseText(styleDataJSON)
				
				//record the data for this car style
				stylesCollectedDataMap[style] = stylesCollectedDataMap[style] + styleAllData  
			}
			
		} catch (Exception ex) {
			logger.error("Fetch Exception: " + ex)
		}
		
		return stylesCollectedDataMap
	}
	
	protected void doHandleFailure(def resp) {
		logger.error("Remote HTTP request failure ${resp.toString()}")
	}
	
	
	private volatile ThreadLocal<WebDriver> webDriverThreadLocal
	private final Object threadLocalLock = new Object()
	private WebDriver getWebDriver() {
		if (webDriverThreadLocal == null) {
			synchronized (threadLocalLock) {
				if (webDriverThreadLocal == null) {
					webDriverThreadLocal = getWebDriverThreadLocal(
						new FirefoxBinary(new File(repoConfig.getFirefoxBinaryPath())),
						new FirefoxProfile(new File(repoConfig.getFirefoxProfilePath())))
				}
			}
		}
		return webDriverThreadLocal.get()
	}
	
	private def getWebDriverThreadLocal(final FirefoxBinary binary, final FirefoxProfile profile) {
		return new ThreadLocal<WebDriver>() {
			protected def initialValue() {
				return new FirefoxDriver(binary, profile)
			}
		}
	}
		
	public static class EdmundsRepositoryConfiguration {
		@Inject
		ScraperConfigService configService

		String getScraperBaseURL() {
			return configService.getApplicationParameter('edmundsBaseURL') as String
		}
		String getMakeJSONSearchURL() {
			return configService.getApplicationParameter('edmundsFindMakeSearchJSONURL') as String
		}
		
		int getNumRetries() {
			return configService.getApplicationParameter('numRetries') as int
		}
		
		String getFirefoxBinaryPath() {
			return configService.getApplicationParameter('firefoxBinaryPath') as String
		}
		String getFirefoxProfilePath() {
			return configService.getApplicationParameter('firefoxProfilePath') as String
		}
		
		String getFireFoxBinaryLocation() {
			return configService.getApplicationParameter('seleniumFireFoxBinaryLocation')
		}
		String getFireFoxProfileLocation() {
			return configService.getApplicationParameter('seleniumFireFoxProfileLocation')
		}
	}
	
}
