source('~/workspace/car-plots/carplots-analysis/src/main/R/carplotsScraperService.R')
source('~/workspace/car-plots/carplots-analysis/src/main/R/analysisFunctions.R')
.jinit(parameters="-Xmx8196m")
carplots.jService <- new(serviceClassName)
setDocumentStore("http://localhost:5984/carplots", carplots.jService)

carplots.buildAndStorePlots(carplots.jService)

#carplots.makeModels <- getMakeModels(carplots.jService)
#carplots.locations <- getSearchLocations(carplots.jService)
##carplots.scraperRuns <- getScraperRuns(carplots.jService)
#iter <- getImportedIterator(makeModelId=10, carplotsService)
#dt <- getImportedFast(iter, carplotsService)
