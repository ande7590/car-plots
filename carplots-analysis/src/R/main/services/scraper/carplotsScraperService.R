serviceClassName <- 'CarplotsScraperService';
defaultServiceJarClasspath <- 
  '/home/mike/workspace/car-plots/carplots-service-analysis/src/main/R/lib/carplots-service-analysis-1.0.jar';
jniServiceClassName <- '' 

# service <- .jnew('com/carplots/service/analysis/CarplotsAnalysisServiceMockImpl')

# makeModels <- .jcall(service, returnSig="Ljava/util/Collection;", method="getMakeModels")
# makeModelArray <- .jcall(makeModels, returnSig="[Ljava/lang/Object;", "toArray")
# testMakeModel <- makeModelArr[[1]]
# .jcall(testMakeModel, returnSig="Ljava/lang/String;", "getMakeName")

#iter <- .jcall(service, returnSig="Ljava/util/Iterator;", method="iterateByMakeId", .jlong(1))
#hasNext <- .jcall(iter, "Z", "hasNext")
#next <- .jcall(iter, "Ljava/lang/Object;", "next")

# Class Definition
setClass(serviceClassName, 
         representation(serviceJarClasspath="character", jservice="jobjRef"),
         prototype(serviceJarClasspath=defaultServiceJarClasspath));

setMethod(f='initialize',
          signature = serviceClassName,
          definition=function(.Object, serviceJarClasspath, jservice) {                      
            
            if (nargs() > 1) {              
              .Object@serviceJarClasspath <- serviceJarClasspath
            } 
            .jinit(classpath=.Object@serviceJarClasspath)  
            
            if (nargs() < 3) {
              .Object@jservice = .jnew(jniServiceClassName)
            } else if (typeof(jservice) == 'jobjRef') {
              .Object@jservice = jservice
            } else {
              stop('Invalid jservice argument')
            }                        
            .Object
          });

# Methods 
setGeneric(name='iterateByExample', 
           function(object, ...) {              
             stop("This object does not support this method")
           });


setMethod('iterateByExample', 
          signature = serviceClassName,
          definition = function(object, example, callback) {
            print('method')
          });


