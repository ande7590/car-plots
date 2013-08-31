package com.carplots.service.analysis;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.MakeModel;

public interface CarplotsAnalysisService {	

	Collection<MakeModel> getMakeModels();
	
	Iterator<Imported> iterateByCarSearchIds(Collection<Long> carSearchIds);
	Iterator<Imported> iterateByModelId(long modelId);
	Iterator<Imported> iterateByModelIdAndZipcode(long modelId, String zipcode);
	Iterator<Imported> iterateByMakeId(long makeId);	
}
