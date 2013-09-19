package com.carplots.scraper.test

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

class SeleniumTestDriver {

	static main(args) {
		def binary = new FirefoxBinary(new File('/usr/lib/firefox/firefox'))
		def profile = new FirefoxProfile(new File('/home/mike/.mozilla/firefox/lg2oje74.default'))
		WebDriver driver = new FirefoxDriver(binary, profile)
		driver.get('http://www.edmunds.com/toyota')
		
		def scrollFunc= '''			
			function fzt52_scrollFunc() {
				var body document.getElementsTagName(
			}
		'''
		
		//Thread.sleep(5000)
		
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getPageSource().contains('</body>')
			};
		})
		
		System.out.println(driver.getPageSource())
	}

}
