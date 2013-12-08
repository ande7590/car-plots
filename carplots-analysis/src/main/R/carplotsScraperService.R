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

getImportedIterator <- function (carplotsAnalysisService, makeModelId=NULL, zipcode=NULL, scraperRunId=NULL) {
  
  iter <- NULL
  if (!is.null(makeModelId) && !is.null(zipcode) && !is.null(scraperRunId)) {
    iter <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Iterator;", method="iterateImported", 
                   .jlong(makeModelId), as.character(zipcode), .jlong(scraperRunId))
  }
  else if (!is.null(makeModelId) && !is.null(zipcode)) {
    iter <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Iterator;", method="iterateImported", 
                   .jlong(makeModelId), as.character(zipcode))
  }
  else if (!is.null(makeModelId) && !is.null(scraperRunId)) {
    iter <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Iterator;", method="iterateImported", 
                   .jlong(makeModelId), .jlong(scraperRunId))
  }
  else if (!is.null(makeModelId)) {
    iter <- .jcall(carplotsAnalysisService@jService, returnSig="Ljava/util/Iterator;", method="iterateImported", 
                   .jlong(makeModelId))
  }
  else {
    stop("Unhandled argument combination")
  }
  iter
}

getImportedFast <- function(jIterator, carplotsAnalysisService) {
  jArr <- .jcall(carplotsAnalysisService@jService, "[Ljava/lang/Object;", "fastIter", jIterator)
  print("Creating data table...")
  colNames <- c("importedId", "listingId", "miles", "price", "year", "engineId", "run_yr", "run_qtr")
  dt <- data.table(matrix(sapply(jArr, .jevalArray), ncol=length(colNames), byrow=TRUE))
  setnames(dt, colNames)
  setkey(dt, "year")
  dt
}

#slow! probably should use getImportedFast
getImported <- function (jIterator, carplotsAnalysisService) {  
  
  getImportedDataTable <- function(imp) {
    imp <- .jcast(imp, "com/carplots/persistence/scraper/entities/Imported")
    data.table (
      importedId = .jcall(imp, returnSig="J", "getImportedId"),
      listingId = .jcall(.jcall(imp, returnSig="Ljava/lang/Long;", "getListingId"), "J", "longValue"),
      miles = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getMiles"), "I", "intValue"),
      price = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getPrice"), "I", "intValue"),
      year = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getCarYear"), "I", "intValue"),
      engineId = .jcall(carplotsAnalysisService@jService, returnSig="J", "getNearestEngineId", imp)
    );
  }
  
  rows <- c()
  while (.jcall(jIterator, "Z", "hasNext")) {    
    rows <- c(rows, list(getImportedDataTable(
      .jcall(jIterator, "Ljava/lang/Object;", "next"))));
  }
  
  
  rbindlist(rows)
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
