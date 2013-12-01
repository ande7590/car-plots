package com.carplots.common.module;

import com.google.inject.PrivateModule;

public abstract class AbstractCarplotsPrivateModule extends PrivateModule {
	
	protected final void configure() {
		doConfigure();
		doExpose();
	}
	
	protected abstract void doConfigure();
	protected abstract void doExpose();
}
