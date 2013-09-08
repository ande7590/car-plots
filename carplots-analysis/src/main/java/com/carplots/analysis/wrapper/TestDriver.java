package com.carplots.analysis.wrapper;

import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;

public class TestDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CarplotsAnalysisServiceImpl test = 
					(new CarplotsAnalysisWrapper()).getCarplotsAnalysisService();
			assert test != null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
