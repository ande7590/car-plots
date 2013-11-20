package com.carplots.persistence.carMeta.test


import com.carplots.persistence.carMeta.module.CarMetaPersistenceInitializationService;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule
import com.google.inject.Guice;
import com.google.inject.Injector;

class TestDriver {
	static void main(String[] args) {
		Injector injector = Guice.createInjector(new CarMetaPersistenceModule());
		final CarMetaPersistenceInitializationService pis =
				injector.getInstance(CarMetaPersistenceInitializationService.class);
		try {
			pis.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
