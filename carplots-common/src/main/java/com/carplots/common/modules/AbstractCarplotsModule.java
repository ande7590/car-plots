package com.carplots.common.modules;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.utilities.GuiceUtility;
import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;

public abstract class AbstractCarplotsModule extends AbstractModule {

	protected void bindInitializationService(Class<? extends InitializationService> clazz)
	{
		bind(InitializationService.class)
			.annotatedWith(GuiceUtility.classNamed(clazz) )
			.to(clazz);			
	}
	
}
