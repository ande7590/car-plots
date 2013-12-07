
MILEAGE_MAX <- 300000 #maximum mileage of record that we will use in analysis, everything else is discarded

cleanCarDataTable <- function(dt) {
  dt <- dt[which(dt$year >= 1914 & dt$year <= 2020 & dt$miles > 1 & dt$miles < MILEAGE_MAX & dt$engineId > -1), ]
  priceIQR <- IQR(dt$price)
  dt <- dt[which(dt$price > 300 & dt$price <= 3.5 * priceIQR), ]
  dt
}

carplots_unique_lowess <- function(dt) {
  lowess(dt$miles_bin, dt$price_aggregate)
}
carplots_bin_apply <- function(dt, binResolution=2500, price_aggregate_fn=mean) {
  
  numBins <- MILEAGE_MAX / binResolution
  binCuts <- binResolution * 1:numBins
  
  setname_lastcol <- function(dt, name) {
    setnames(dt, c(names(dt)[1:(length(names(dt)) - 1)], name))
  }  
  price_aggregate_fn_wrapper <- function(dt_bin) {
    dt_bin <- cbind(dt_bin, aggregate_fn(dt_bin$price))
    setname_lastcol(dt_bin, "price_aggregate")
    dt_bin
  }  
  each_engine <- function(dt_bin) {    
    dt_bin <- cbind(dt_bin, cut(dt_bin$miles, mileageBins, labels=FALSE))
    setname_lastcol(dt_bin, "miles_bin")
    Reduce(rbind, by(dt_bin, dt$miles_bin, price_aggregate_fn_wrapper))
  }
  each_year <- function(dt_yr) {
    dt_yr$engineId <- factor(dt_yr$engineId)
    Reduce(rbind, data.table(by(dt_yr, dt_yr$engineId, each_engine)))
  }
  dt$year <- factor(dt$year)
  Reduce(rbind, by(dt, dt$year, each_year))
}
