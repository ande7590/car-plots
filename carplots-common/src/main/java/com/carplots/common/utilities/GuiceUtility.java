package com.carplots.common.utilities;

import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class GuiceUtility 
{
	private GuiceUtility() {}
	
	public static Named classNamed(Class<? extends Object> clazz)
	{
		return Names.named(clazz.getName());
	}
}
