package com.carplots.service.analysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.MakeModel;

public class CarplotsAnalysisServiceMockImpl implements CarplotsAnalysisService {

	private final Collection<MakeModel> mockMakeModelData;
	private final Collection<Imported> mockImportedData;
	
	public CarplotsAnalysisServiceMockImpl() {
				
		final MakeModel mockMakeModel = new MakeModel();
		mockMakeModel.setMakeModelId(3);
		mockMakeModel.setMakeId(44);		
		mockMakeModel.setMakeName("FakeMake");
		mockMakeModel.setModelId(555);
		mockMakeModel.setModelName("Fake Model");
		mockMakeModelData = Arrays.asList(new MakeModel[] { mockMakeModel });
					
		final Imported mockImported = new Imported();
		mockImported.setBodyStyle("body");
		mockImported.setCarName("carname");
		mockImported.setCarYear(1945);
		mockImported.setDealerPhone("123-444-3333");
		mockImported.setMiles(1000);
		mockImported.setPrice(23000);
		mockImported.setImportedId(1);
		mockImportedData = Arrays.asList(new Imported[] { mockImported });
	}
	
	@Override
	public Collection<MakeModel> getMakeModels() {
		return mockMakeModelData;
	}

	@Override
	public Iterator<Imported> iterateByCarSearchIds(
			Collection<Long> carSearchIds) {
		return mockImportedData.iterator();
	}

	@Override
	public Iterator<Imported> iterateByModelId(long modelId) {
		return mockImportedData.iterator();
	}

	@Override
	public Iterator<Imported> iterateByModelIdAndZipcode(long modelId,
			String zipcode) {
		return mockImportedData.iterator();
	}

	@Override
	public Iterator<Imported> iterateByMakeId(long makeId) {
		return mockImportedData.iterator();
	}


}
