library('rJava')
library('data.table')
library('lubridate')

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
    #getting requires cast, tostring
    scr_casted <- .jcast(scraperRunsArray[[1]], "Lcom/carplots/persistence/scraper/entities/ScraperRun")
    scr_date <- .jcall(scr_casted, returnSig="Ljava/util/Date;", "getScraperRunDt")
    scr_date_str <- .jcall(scr_date, returnSig="Ljava/lang/String;", "toString")    
    data.table(
      scraperRunId = .jcall(scr, returnSig="J", "getScraperRunId"),
      scraperRunDt = ymd_hms(scr_date_str)
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

getImported <- function (jIterator) {  
  
  getImportedDataTable <- function(imp) {
    imp <- .jcast(imp, "com/carplots/persistence/scraper/entities/Imported")
    data.table (
      importedId = .jcall(imp, returnSig="J", "getImportedId"),
      listingId = .jcall(.jcall(imp, returnSig="Ljava/lang/Long;", "getListingId"), "J", "longValue"),
      miles = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getMiles"), "I", "intValue"),
      price = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getPrice"), "I", "intValue"),
      year = .jcall(.jcall(imp, returnSig="Ljava/lang/Integer;", "getCarYear"), "I", "intValue")
    );
  }
  
  rows <- c()
  while (.jcall(jIterator, "Z", "hasNext")) {    
    rows <- c(rows, list(getImportedDataTable(
      .jcall(jIterator, "Ljava/lang/Object;", "next"))));
  }
  
  
  rbindlist(rows)
}

#iter <- .jcall(service, returnSig="Ljava/util/Iterator;", method="iterateByMakeId", .jlong(1))
#hasNext <- .jcall(iter, "Z", "hasNext")
#next <- .jcall(iter, "Ljava/lang/Object;", "next")