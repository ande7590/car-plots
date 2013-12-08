source('~/workspace/car-plots/carplots-analysis/src/main/R/carplotsScraperService.R')
source('~/workspace/car-plots/carplots-analysis/src/main/R/analysisFunctions.R')
.jinit(parameters=c("-XX:-LoopUnswitching", "-Xmx4096m", "-XX:+UseG1GC", "-XX:+UseCompressedStrings", "-XX:+AggressiveOpts"))
carplots.jService <- new(serviceClassName)
setDocumentStore("http://localhost:5984/carplots", carplots.jService)

args <- commandArgs(trailingOnly = TRUE)
for (i in 1:length(args)) {  
  makeName <- args[i];
  print(paste("Beginning work on ", makeName))
  carplots.buildAndStorePlots(carplots.jService, makeName)  
}

#carplots.makeModels <- getMakeModels(carplots.jService)
#carplots.locations <- getSearchLocations(carplots.jService)
##carplots.scraperRuns <- getScraperRuns(carplots.jService)
#iter <- getImportedIterator(makeModelId=10, carplotsService)
#dt <- getImportedFast(iter, carplotsService)
