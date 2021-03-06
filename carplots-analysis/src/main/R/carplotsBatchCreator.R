#load carplots analysis and data retrieval functions
source('~/workspace/car-plots/carplots-analysis/src/main/R/carplotsScraperService.R')
source('~/workspace/car-plots/carplots-analysis/src/main/R/analysisFunctions.R')

#init JVM and document store
.jinit(parameters=c("-XX:-LoopUnswitching", "-Xmx8096m", "-XX:+UseG1GC", "-XX:+UseCompressedStrings", "-XX:+AggressiveOpts"))
carplots.jService <- new(serviceClassName)
setDocumentStore("http://localhost:5984/carplots_staging", carplots.jService)

#If invoked via command line, read and process command line arguments
#args <- c('Acura')
args <- commandArgs(trailingOnly = TRUE)
if (length(args) > 0) {
  byRunYear = args[1] == "TRUE"
  for (i in 2:length(args)) {  
    makeName <- args[i];
    print(paste("Beginning work on ", makeName))
    carplots.buildAndStorePlots(carplots.jService, makeName, byRunYear=byRunYear)  
  }
}
