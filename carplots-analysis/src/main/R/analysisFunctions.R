
carplots.MILEAGE_MAX <- 200000 #maximum mileage of record that we will use in analysis, everything else is discarded
carplots.NUM_RESAMPLE <- 100
carplots.MINIMUM_DATASET_SIZE <- 50
carplots.MINIMUM_MILEAGE_BINS <- 10
carplots.IQR_OUTLIER_FACTOR = 2.0

carplots.clean <- function(dt) {
  dt <- dt[which(dt$year >= 1914 & dt$year <= 2020 & dt$miles > 1 & dt$miles < carplots.MILEAGE_MAX), ]
  if (nrow(dt[engineId < 0, ]) < nrow(dt)) {
    dt <- dt[engineId > 0, ]
  }  
  outlier_threshold <- quantile(dt$price)[4] + carplots.IQR_OUTLIER_FACTOR * IQR(dt$price)
  if (outlier_threshold > 0) {
    dt <- dt[which(dt$price > 300 & dt$price <= outlier_threshold), ]
  }
  dt
}

carplots.price_aggregate.strip_outliers <- function(price) {
  price_iqr <- IQR(price)
  mean(price[which(price < 2.0 * price_iqr)])
}

carplots.price_aggregate.resample_mean <- function(price) {
  result <- c()
  for (i in 1:carplots.NUM_RESAMPLE) {
    result <- c(mean(sample(price, length(price), replace=TRUE)))
  }
  mean(result)
}

carplots.plot_default <- function() {
  plot.default(x=c(), y=c(), xlim=c(1,205000), xlab="Miles", ylim=c(1,50000), ylab="Price")
}

#if a point isn't monotonically decreasing,
#compute a taylor series approximation based on the delta
# and value of the previous point (i.e. a known "good" point)
carplots.decreasing_taylor_smoother <- function(xy_pair) {  
  if (length(xy_pair$x) > 1) {       
    last_delta <- 0
    for(i in 2:(length(xy_pair$x))) {     
      #if a point isn't decreasing
      if (xy_pair$y[i-1] < xy_pair$y[i]) {
        #take the minimum of the last point, or (hopefully) a taylor series approximation what the value
        #should be
        xy_pair$y[i] <- min(xy_pair$y[i-1], xy_pair$y[i-1] + .5 * (xy_pair$x[i] - xy_pair$x[i - 1]) * last_delta  )
      }
      last_delta <- (xy_pair$y[i] - xy_pair$y[i - 1]) / (xy_pair$x[i] - xy_pair$x[i - 1])             
    }      
  }
  xy_pair  
}

carplots.create <- function(dt, plot=FALSE, plot_overlay=FALSE, process_fn=function(x) {}, ignoreError=FALSE, byRunYear=FALSE) {  
  
  if (is.null(dt$miles_bin) || is.null(dt$price_aggregate)) {
    if (ignoreError == TRUE) {
      return 
    } else {
      stop("Data table must have both miles_bin and price_aggregate columns, one or both was missing.")
    }
  }
  
  if (plot == TRUE && plot_overlay == FALSE) {
    carplots.plot_default();
  }
	
	#use a rainbow for diagnostic plots, i.e. the graph should look like a rainbow
	#since the price should decrease (w/ everything else held constant) with the car's year.
	#i.e. the partial derivative of price w.r.t the car's year is a decreasing function
  colors <- rainbow(50);

	#some globals for the processing functions below
  carplots_legend_desc <<- c()
  carplots_col_iter <<- 1;

	#the main processing function, once we have sliced and diced the 
	#car by year, engine, etc, we call this to generate a plot (or points)
	process_car_plot <- function(dt_car) {
      if ( nrow(dt_car) >= carplots.MINIMUM_DATASET_SIZE &&
          length(unique(dt_car$miles_bin)) >= carplots.MINIMUM_MILEAGE_BINS) {        
        dt_car <- dt_car[miles <= carplots.MILEAGE_MAX, ]        
        car_pts <- dt_car[, unique(price_aggregate), by=miles_bin]          
        car_lowess <- lowess(car_pts$miles_bin, car_pts$V1)              
        #add bogus point onto end so decreasing_taylor_smoother will extrapolate if necessary
        car_lowess$x <- c(car_lowess$x, carplots.MILEAGE_MAX + 1)
        car_lowess$y <- c(car_lowess$y, .Machine$integer.max)
        car_lowess <- carplots.decreasing_taylor_smoother(car_lowess)  
        if (plot) {
          lines(car_lowess$x, car_lowess$y , col=colors[carplots_col_iter])
          carplots_col_iter <<- carplots_col_iter + 1
          carplots_legend_desc <<- c(carplots_legend_desc, paste(dt_car$year[1]), collapse=" "); 
        } else {
					#callback for external function to process this piece of sliced data (e.g. store it).
					#re-slicing the data table returned (below) by the outer-loops is expensive
          process_fn(dt_car, car_lowess)
        }
        car_lowess #<-outer-loops return an aggregated data table with
      }      
	}
    
	
	#slice and dice
	#for sure we will use the car year and engine as strata
  
  #slice on car year
  by(dt, dt$year, function(dt_year) {
    #slice each car by it's engine (i.e. different engines in same car cost different amounts)
    by(dt_year, dt_year$engineId, function(dt_engine) {
      if (byRunYear == TRUE) {
        #slice by which year we collected the data too
        by(dt_engine, dt_engine$run_yr, function(dt_run_yr) {
          process_car_plot(dt_run_yr);
        });
      } else {
        process_car_plot(dt_engine);
		  }
    });
  });
  
  #if (plot == TRUE) {
  #  legend("topright", legend = carplots_legend_desc,
  #         text.width = strwidth("1,000,000"),
  #         xjust = 1, yjust = 1, pch=1,
  #         title = "Plots", col=colors[1:length(carplots_legend_desc)], cex=.75, ncol=3); 
  #}
}

carplots.apply <- function(dt, binResolution=5000, price_aggregate_fn=mean, 
                               reduceYear=TRUE, reduceEngineId=TRUE) {
  
  numBins <- carplots.MILEAGE_MAX / binResolution
  binCuts <- binResolution * (1:numBins)
  
  setname_lastcol <- function(dt, name) {
    setnames(dt, c(names(dt)[1:(length(names(dt)) - 1)], name))
  }  
  cond_reduce <- function(condition, dt, reduceFn=rbind) {
    if (condition) { Reduce(reduceFn, dt); } 
    else { dt; }
  }  
  price_aggregate_fn_wrapper <- function(dt_bin) {
    dt_bin <- cbind(dt_bin, price_aggregate_fn(dt_bin$price))
    setname_lastcol(dt_bin, "price_aggregate")
    dt_bin
  }    
  each_engine <- function(dt_bin) {    
    dt_bin <- cbind(dt_bin, binResolution * cut(dt_bin$miles, binCuts, labels=FALSE))
    setname_lastcol(dt_bin, "miles_bin")
    Reduce(rbind, by(dt_bin, dt_bin$miles_bin, price_aggregate_fn_wrapper))
  }
  each_year <- function(dt_yr) {
    dt_yr$engineId <- factor(dt_yr$engineId)    
    cond_reduce(reduceEngineId, by(dt_yr, dt_yr$engineId, each_engine))
  }
  dt$year <- factor(dt$year)
    
  retVal <- cond_reduce(reduceYear, by(dt, dt$year, each_year))
  #adjust bin labels to be bin start rather than end and adjust for off-by-one with labels
  retVal$miles_bin = retVal$miles_bin - (2 * binResolution)
  retVal
}

carplots.buildAndStorePlots <- function(service, makeFilter) {
  
  carplots.makeModels <- getMakeModels(service)  
  makesToProcess <- carplots.makeModels[makeName == makeFilter, ]
  
  print(paste("Processing ", nrow(makesToProcess), " models"))
  
  for (i in 1:nrow(makesToProcess)) {
    makeModel <- makesToProcess[i]
    #out <- tryCatch( 
    #{
      print(paste(c("fetching ", makeModel), collapse=" "))
      dt <- getImported(makeModelId=makeModel$makeModelId, service)      
      dt <- carplots.clean(dt)
      print(paste("creating plot from ", nrow(dt), " data points"))
      if (nrow(dt) > 0) {
        dt <- carplots.apply(dt)
        carplots.create(dt, byRunYear=TRUE, process_fn=function(car_dt, pt_data) {
          print("storing plot")
          plot_document <- list(
            makeModelId=makeModel$makeModelId,
            type="price_vs_miles",
            collection_years=unique(car_dt$run_yr),
            car_years = unique(car_dt$year),
            car_engine = unique(car_dt$engineId),
            point_data=pt_data);
          createDocument(document=plot_document, carplotsAnalysisService=service)
        })        
      }
      print("done")            
    #},
    #error = function(cond) {      
    #  createDocument(document=list(makeModelId=makeModel$makeModelId, type="price_vs_miles", error=TRUE), carplotsAnalysisService=service)
    #},
    #warning=function(cond) {},
    #finally = {}
    #);
    
    
  }
}
