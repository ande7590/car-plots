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
			final Iterator<Imported> importedIter = service.iterateImported(6);
			while (importedIter.hasNext()) {
				final Imported i = importedIter.next();
				System.out.println(service.getNearestEngineId(i));
			}
		} catch (Exception e) {
			System.out.println("ERROR creating/getting service");
			e.printStackTrace();
		}
		System.out.println("Test driver finished");
	}

}