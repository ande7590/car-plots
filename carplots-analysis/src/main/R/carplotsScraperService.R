library('rJava')
library('data.table')
library('rjson')

serviceClassName <- 
  'CarplotsAnalysisService';
wrapperJavaClassName <- 
  'com/carplots/analysis/wrapper/CarplotsAnalysisWrapper' 
defaultServiceJarClasspath <- 
  '/home/mike/workspace/car-plots/carplots-analysis/src/main/R/libs/carplots-analysis-1.0.jar';

# Class Definition
setClass(serviceClassName, 
         representation(serviceJarClasspath="character", jWrapper="jobjRef", jService="jobjRef"),
         prototype(serviceJarClasspath=defaultServiceJarClasspath));

setMethod(f='initialize',
          signature = serviceClassName,
          definition=function(.Object, serviceJarClasspath=defaultServiceJarClasspath) {                      
            
            print('running init');
            
            if (nargs() > 1) {              
              .Object@serviceJarClasspath <- serviceJarClasspath
            } 
            else if (!file.exists(serviceJarClasspath)) {
              stop(paste("Couldn't load service jar, file not found: ", serviceJarClasspath))
            }
            
            .jinit(classpath=serviceJarClasspath)  
            
            if (nargs() < 3) {
              .Object@jWrapper <- .jnew(wrapperJavaClassName)
              .Object@jService <- .jcall(.Object@jWrapper, returnSig="Lcom/carplots/service/analysis/CarplotsAnalysisServiceImpl;", 
                                         "getCarplotsAnalysisService")
            } else {
              stop('Invalid jservice argument')
            }                        
            .Object
          });


getMakeModels <- function(carplotsAnalysisService) {
  
  makeModels <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Collection;", "getMakeModels")
  makeModelArray <- .jcall(makeModels, returnSig="[Ljava/lang/Object;", "toArray") 
  
  getMakeModelDataTable <- function(mm) {
      data.table(
        makeModelId = .jcall(mm, returnSig="J", "getMakeModelId"),
        parentMakeModelId = .jcall(mm, returnSig="J", "getParentMakeModelId"),
        makeId = .jcall(mm, returnSig="J", "getMakeId"),
        modelId = .jcall(mm, returnSig="J", "getModelId"),
        makeName = .jcall(mm, returnSig="Ljava/lang/String;", "getMakeName"),
        modelName = .jcall(mm, returnSig="Ljava/lang/String;", "getModelName")
      );    
  }
  
  rbindlist(lapply(makeModelArray, getMakeModelDataTable))
}

getSearchLocations <- function (carplotsAnalysisService) {
  
  searchLocations <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Collection;", "getSearchLocations") 
  searchLocationsArray <- .jcall(searchLocations, returnSig="[Ljava/lang/Object;", "toArray") 
  
  getLocationDataTable <- function(loc) {
    data.table(
      zipcode = .jcall(loc, returnSig="Ljava/lang/String;", "getZipcode"),
      city = .jcall(loc, returnSig="Ljava/lang/String;", "getCity"),
      state = .jcall(loc, returnSig="Ljava/lang/String;", "getState")
    );
  }
  
  rbindlist(lapply(searchLocationsArray, getLocationDataTable))
}

getScraperRuns <- function (carplotsAnalysisService) {
  
  scraperRuns <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Collection;", "getScraperRuns") 
  scraperRunsArray <- .jcall(scraperRuns, returnSig="[Ljava/lang/Object;", "toArray") 
  
  getScraperRunsDataTable <- function(scr) {
    data.table(
      scraperRunId = .jcall(scr, returnSig="J", "getScraperRunId"), 
      scraperRunDt = as.Date(.jcall(scr, returnSig="Ljava/lang/String;", "getScraperRunDateString"))
    );
  }
  
  rbindlist(lapply(scraperRunsArray, getScraperRunsDataTable))
}

getImported <- function(makeModelId, carplotsAnalysisService) {
  jArr <- .jcall(carplotsAnalysisService@jService, "[Ljava/lang/Object;", "getImported", .jlong(makeModelId))
  print("Creating data table...")
  colNames <- c("importedId", "listingId", "miles", "price", "year", "engineId", "run_yr", "run_qtr")
  dt <- data.table(matrix(sapply(jArr, .jevalArray), ncol=length(colNames), byrow=TRUE))
  setnames(dt, colNames)
  setkey(dt, "year")
  dt
}

setDocumentStore <- function(docStoreURL, carplotsAnalysisService) {  
  .jcall(carplotsAnalysisService@jService, returnSig="V", "setDocumentStore", docStoreURL)  
}

createDocument <- function(document, carplotsAnalysisService) {
  .jcall(carplotsAnalysisService@jService, returnSig="S", "createDocument", toJSON(document))
}

updateDocument <- function(document, carplotsAnalysisService) {
  .jcall(carplotsAnalysisService@jService, returnSig="S", "updateDocument", toJSON(document))
}

clearEntityCache <- function(carplotsAnalysisService) {
  .jcall(carplotsAnalysisService@jService, returnSig="V", "clear")
}
