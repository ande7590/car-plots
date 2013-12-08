package com.carplots.analysis.wrapper;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;

public class TestDriver {

	public static void main(String[] args) {
		CarplotsAnalysisServiceImpl service = null;
		try {
			service = (new CarplotsAnalysisWrapper()).getCarplotsAnalysisService();
			service.getImported(10);
		} catch (Exception e) {
			System.out.println("ERROR creating/getting service");
			e.printStackTrace();
		}
		System.out.println("Test driver finished");
	}

}
