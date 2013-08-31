package com.carplots.service.analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.MakeModelDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.Search;
import com.google.inject.Inject;

public class CarplotsAnalysisServiceImpl implements CarplotsAnalysisService {

	@Inject
	MakeModelDao makeModelDao;
	
	@Inject
	ImportedDao importedDao;
	
	@Inject
	SearchDao searchDao;
	
	@Override
	public Collection<MakeModel> getMakeModels() {
		final Collection<MakeModel> makeModels = new LinkedList<MakeModel>(); 
		final Iterator<MakeModel> iterMakeModels = makeModelDao.iterateAll();
		while (iterMakeModels.hasNext()) {
			makeModels.add(iterMakeModels.next());
		}		
		return makeModels;
	}
	@Override
	public Iterator<Imported> iterateByModelId(long modelId) {
		return null;
	}

	@Override
	public Iterator<Imported> iterateByMakeId(long makeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Imported> iterateByCarSearchIds(
			Collection<Long> carSearchIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Imported> iterateByModelIdAndZipcode(long modelId,
			String zipcode) {
		// TODO Auto-generated method stub
		return null;
	}

}
