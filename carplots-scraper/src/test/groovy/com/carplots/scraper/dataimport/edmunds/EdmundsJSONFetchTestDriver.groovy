package com.carplots.scraper.dataimport.edmunds

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.scraper.test.module.EdmundsJSONFetchModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

class EdmundsJSONFetchTestDriver {

	static Logger logger = LoggerFactory.getLogger(EdmundsJSONFetchTestDriver.class)
	
	def cleanStr(def str) {
		def foo = 'bar'
		return (str != null)? str.replace("'", "").replace("\\", "") : "";
	}
	
	void run() {
		
		//load dependency injection framework
		Injector injector = Guice.createInjector(new EdmundsJSONFetchModule())
		
		EdmundsRepository repo = injector.getInstance(EdmundsRepository.class)
		StringBuilder sb = new StringBuilder()
				
		EdmundsRepositoryImpl.edmundsMakesJSON.each { makeName ->
			def jsonData = repo.getMakeJSON(makeName)
			def models = jsonData["models"] 
			jsonData["models"].each { modelDataKey, modelData ->
				if (!modelDataKey.contains(':') || modelDataKey.endsWith(':')) {
					modelDataKey = '${modelDataKey}:none'
				}			
				try {	
					def (modelDesc, bodyDesc) = modelDataKey.split(':')
					modelDesc = cleanStr(modelDesc)
					bodyDesc = cleanStr(bodyDesc)
					def modelName = cleanStr(modelData["modelname"])
					def submodel = cleanStr(modelData["submodel"])
					def link = cleanStr(modelData["link"])
					def bodyTypes = modelData['bodytypes']
					def years = modelData['years']
					bodyTypes.each { bodyType ->
						bodyType = cleanStr(bodyType)
						years["USED"].each { yr ->
							sb.append("INSERT INTO Edmunds.Imported (MakeName, ModelName, Year, SubModelDesc, EdmundsLink, BodyDesc) VALUES ('${makeName}', '${modelName}', '${yr}', '${submodel}', '${link}', '${bodyType}');\n");
						}
					}
				} catch (Exception ex) {
					def foo = 'bar'
				}				
			}
		}
		
		(new File('/home/mike/edmunds_sql.sql')).append(sb.toString())
			
	}
	
	static main(args) {
		logger.debug('Starting program')
		(new EdmundsJSONFetchTestDriver()).run()
		logger.debug('Finishing program')
	}

}
