
carplots.MILEAGE_MAX <- 200000 #maximum mileage of record that we will use in analysis, everything else is discarded
carplots.NUM_RESAMPLE <- 100
carplots.MINIMUM_DATASET_SIZE <- 50
carplots.MINIMUM_MILEAGE_BINS <- 10

carplots.clean <- function(dt) {
  dt <- dt[which(dt$year >= 1914 & dt$year <= 2020 & dt$miles > 1 & dt$miles < MILEAGE_MAX & dt$engineId > -1), ]
  priceIQR <- IQR(dt$price)
  dt <- dt[which(dt$price > 300 & dt$price <= 3.5 * priceIQR), ]
  dt
}

carplots.price_aggregate.strip_outliers <- function(price) {
  price_iqr <- IQR(price)
  mean(price[which(price < 2.0 * price_iqr)])
}

carplots.price_aggregate.resample_mean <- function(price) {
  result <- c()
  for (i in 1:NUM_RESAMPLE) {
    result <- c(mean(sample(price, length(price), replace=TRUE)))
  }
  mean(result)
}

carplots.plot_default <- function() {
  plot.default(x=c(), y=c(), xlim=c(1,300000), xlab="Miles", ylim=c(1,80000), ylab="Price")
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

carplots.create <- function(dt, year, plot=FALSE, plot_overlay=FALSE, process_fn=function(x) {}) {  
  
  if (plot == TRUE && plot_overlay == FALSE) {
    carplots_default();  
  }
    
  colors <- rainbow(50);
  carplots_legend_desc <<- c()
  carplots_col_iter <<- 1;
  by(dt, dt$year, function(dt_year) {
    by(dt_year, dt_year$engineId, function(dt_engine) {
      if ( nrow(dt_engine) >= MINIMUM_DATASET_SIZE &&
          length(unique(dt_engine$miles_bin)) >= MINIMUM_MILEAGE_BINS) {        
        dt_engine <- dt_engine[miles <= MILEAGE_MAX, ]
        car_pts <- dt_engine[, unique(price_aggregate), by=miles_bin]          
        car_lowess <- lowess(car_pts$miles_bin, car_pts$V1)              
        #add bogus point onto end so decreasing_taylor_smoother will extrapolate if necessary
        car_lowess$x <- c(car_lowess$x, MILEAGE_MAX + 1)
        car_lowess$y <- c(car_lowess$y, .Machine$integer.max)
        car_lowess <- decreasing_taylor_smoother(car_lowess)        
        if (plot == TRUE) {
          lines(car_lowess$x, car_lowess$y , col=colors[carplots_col_iter])
          carplots_col_iter <<- carplots_col_iter + 1
          carplots_legend_desc <<- c(carplots_legend_desc, paste(dt_engine$year[1]), collapse=" "); 
        } else {
          process_fn(car_lowess)
        }
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

carplots.bin_apply <- function(dt, binResolution=7500, price_aggregate_fn=mean, 
                               reduceYear=TRUE, reduceEngineId=TRUE) {
  
  numBins <- MILEAGE_MAX / binResolution
  binCuts <- binResolution * 1:numBins
  
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
    dt_bin <- cbind(dt_bin, binResolution * cut(dt_bin$miles, mileageBins, labels=FALSE))
    setname_lastcol(dt_bin, "miles_bin")
    Reduce(rbind, by(dt_bin, dt_bin$miles_bin, price_aggregate_fn_wrapper))
  }
  each_year <- function(dt_yr) {
    dt_yr$engineId <- factor(dt_yr$engineId)    
    cond_reduce(reduceEngineId, by(dt_yr, dt_yr$engineId, each_engine))
  }
  dt$year <- factor(dt$year)
  cond_reduce(reduceYear, by(dt, dt$year, each_year))
}
