package com.carplots.common.utilities;

import java.net.URL;
import java.net.URLClassLoader;

public class DebugUtility {
	public static void printClasspath()
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		 
        URL[] urls = ((URLClassLoader)cl).getURLs();
 
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
	}
}
