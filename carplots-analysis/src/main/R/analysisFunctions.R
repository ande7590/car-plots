cleanCarDataTable <- function(dt) {
  dt <- dt[which(dt$year >= 1914 & dt$year <= 2020 & dt$miles > 1 & dt$miles < 300000 & dt$engineId > -1), ]
  priceIQR <- IQR(dt$price)
  dt <- dt[which(dt$price > 300 & dt$price <= 3.5 * priceIQR), ]
  dt
}

carplots_apply <- function(dt, each_plot=function(dt_plot) { lowess(dt_plot$miles, dt_plot$price) }) {
  each_year <- function(dt_yr) {
    dt_yr$engineId <- factor(dt_yr$engineId)
    by(dt_yr, dt_yr$engineId, each_plot)
  }
  dt$year <- factor(dt$year)
  by(dt, dt$year, each_year)
}
