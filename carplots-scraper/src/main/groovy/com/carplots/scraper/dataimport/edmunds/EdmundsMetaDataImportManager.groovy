package com.carplots.scraper.dataimport.edmunds

import javassist.bytecode.stackmap.BasicBlock.Catch;

import org.slf4j.LoggerFactory;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.persistence.carMeta.entities.CarEngine;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.carMeta.entities.CarTrim;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.service.carMeta.CarMetaService;
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

class EdmundsMetaDataImportManager implements DataImportManager {

	org.slf4j.Logger logger = LoggerFactory.getLogger(EdmundsMetaDataImportManager.class)
	
	@Inject @com.carplots.common.module.CarMeta
	InitializationService carMetaInitSvc
	
	@Inject @com.carplots.common.module.Scraper
	InitializationService scraperInitSvc
	
	@Inject
	ScraperConfigService configService
	
	@Inject
	CarMetaService carMetaService
	
	@Inject
	CarplotsScraperService carScraperService
	
	@Inject
	EdmundsRepository edmundsRepository
	
	@Transactional()
	@Override
	public void importData() {
		try {
			carMetaInitSvc.start()
			scraperInitSvc.start()
			doImport()
		}
		finally {
			try {
				carMetaInitSvc.stop()
			} catch (Exception ex) {
				logger.error("Error stopping carMetaService: ", ex)
			}
			try {
				scraperInitSvc.stop()
			} catch (Exception ex) {
				logger.error("Error stopping scraperService: ", ex)
			}
		}
		
	}
	
	private void doImport() {
								
		EdmundsRepositoryImpl.edmundsMakesJSON.each { makeName ->
			def jsonData = edmundsRepository.getMakeJSON(makeName)
			def models = jsonData["models"]
			
			jsonData["models"].each { modelDataKey, modelData ->
				if (!modelDataKey.contains(':') || modelDataKey.endsWith(':')) {
					modelDataKey = '${modelDataKey}:none'
				}
				def link = cleanStr(modelData["link"])
				def bodyTypes = modelData['bodytypes']
				def years = modelData['years']
				bodyTypes.each { bodyType ->
					bodyType = cleanStr(bodyType)
					years["USED"].each { yr ->
						def linkParts = link.split('/')
						def (make, model, year) = [linkParts[1], linkParts[2], "$yr" as String]
						def edmundsData = edmundsRepository.getMakeModelYearJSON(
								make, model, year)
						def isFirst = true
						CarModel carModel = null
						edmundsData.each { edmundsId, dataMap ->
							if (isFirst) {
								MakeModel makeModel = getMakeModel(make, model)
								carModel = new CarModel(
									makeName: make,
									modelName: model,
									year: year as Integer,
									modelType: bodyType,
									makeModelId: makeModel.getMakeModelId(),
									trims: [])
								isFirst = false
							}
							def mpg = (dataMap['Fuel Economy'] =~ /[^0-9\/]/).replaceAll('').trim().split('/')
							if (mpg.size() < 2) {
								mpg = [null, null]
							}
							CarTrim carTrim = new CarTrim(
								driveTrain: dataMap['Drivetrain'],
								transmission: dataMap['Transmission'],
								trimName: (dataMap['desc'] == null)? '' : dataMap['desc'],
								mpgCity: stripNonNumericChars(mpg[0]) as Integer,
								mpgHighway: stripNonNumericChars(mpg[1]) as Integer,
								engines: [])
							carModel.trims.add(carTrim)
							
							def engine = dataMap['Engine']
							def engineMatch = (engine =~ /(?i).*?([0-9\.]+\s*L).*?/)
							def cylinderMatch = (engine =~ /(?i).*?(\d+[ -]*cyl).*?/)
							
							def horsepower = dataMap['Horse Power']
							def hpMatch = (horsepower =~ /(?i).*?([0-9]+\s*h).*?/)
							
							if (!engineMatch.matches() || !cylinderMatch.matches()) {
								logger.warn("Cannot find engine for $make, $model, $year: $engine")
							}
							else {
								def hp = 0
								if (!hpMatch.matches()) {
									logger.warn("Cannot find horsepower for $make, $model, $year: $horsepower")
								}
								else {
									hp = stripNonNumericChars(hpMatch[0][1]) as Integer
								}
								CarEngine carEngine = new CarEngine(
									cylinders: stripNonNumericChars(cylinderMatch[0][1]) as Integer,
									description: engine,
									displacementCC: ((stripNonNumericChars(engineMatch[0][1]) as Double) * 1000) as Integer,
									horsepower: hp)
								carTrim.engines.add(carEngine)
							}
						}
						carMetaService.createModel(carModel)
					}
				}
			}
		}
			
	}
	def blankNull (def str) {
		return (str == null)? '' : str
	}
	
	def stripNonNumericChars(def str) {
		def cleaned = (str =~ /[^0-9.]/).replaceAll('')
		return (cleaned == '')? '0' : cleaned
	}
	
	def getKey(def str) {
		return (str.toLowerCase() =~ /[^A-Za-z0-9]/).replaceAll('').trim()
	}
	
	private def makeModelMap = null
	private def missingMakeMap = [:]
	private def makeModelCache = [:]
	private MakeModel getMakeModel(def makeName, def modelName) {
		
		def cacheKey = getKey(makeName) + '_' + getKey(modelName)
		if (makeModelCache.containsKey(cacheKey)) {
			return makeModelCache[cacheKey]
		}
		
		
		if (makeModelMap == null) {
			makeModelMap = [:]
			
			for (MakeModel mm : carScraperService.iterateMakeModels()) {
				def makeNameKey = getKey(mm.makeName)
				if (!makeModelMap.containsKey(makeNameKey)) {
					makeModelMap[makeNameKey] = [:]
				}
				def modelNameKey = getKey(mm.modelName)
				makeModelMap[makeNameKey].put(modelNameKey, mm)
			}
		}
		
		def makeKey = getKey(makeName)
		if (!makeModelMap.containsKey(makeKey)) {
			if (!missingMakeMap.containsKey(makeKey)) {
				def mostSimilarKey = null
				def mostSimilarDist = Integer.MAX_VALUE
				makeModelMap.each { key, val ->
					def similarity = getLevenshteinDistance(key, makeKey)
					if (similarity < mostSimilarDist) {
						mostSimilarKey = key
						mostSimilarDist = similarity
					}
				}
				missingMakeMap[makeKey] = mostSimilarKey
			}
			makeKey = missingMakeMap[makeKey]
		}
		
		def modelMap = makeModelMap[makeKey]
		def modelKey = getKey(modelName)
		if (!modelMap.containsKey(modelKey)) {
			def mostSimilarKey = null
			def mostSimilarDist = Integer.MAX_VALUE
			modelMap.each { key, value ->
				def similarity = getLevenshteinDistance(key, modelKey)
				if (similarity < mostSimilarDist) {
					mostSimilarKey = key
					mostSimilarDist = similarity
				}
			}
			modelKey = mostSimilarKey
		}
		
		makeModelCache[cacheKey] = makeModelMap[makeKey][modelKey]
		
		return makeModelCache[cacheKey]
		
	}

	def cleanStr(def str) {
		return (str != null)? str.trim() : "";
	}
	
	@Override
	public boolean isImportSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
