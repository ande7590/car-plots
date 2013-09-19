package com.carplots.scraper.cache

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import sun.misc.IOUtils

public class ZipFileCache
	implements Cache<String> {

	public ZipFileCache(final String cacheDirectory) {
		this.cacheDirectory = cacheDirectory
		setupCache()
	}
	
	public String getCachedEntry(final String entryName) {
		def entry = null			
		def entryPath = getEntryPath(entryName)
		def cacheFile = new File(entryPath)
		if (cacheFile.exists()) {
			def is = new FileInputStream(entryPath)
			def zipIs = new ZipInputStream(is)
			zipIs.getNextEntry()
			entry = new String(org.apache.commons.io.IOUtils.toByteArray(zipIs),
				"UTF-8")
		}
		return entry
	}
	
	public void setCachedEntry(final String entryName, final String entry) {
		def entryPath = getEntryPath(entryName)
		def os = new FileOutputStream(entryPath)
		def zipOs = new ZipOutputStream(os)
		try {
			def zipEntry = new ZipEntry(entryName)
			zipOs.putNextEntry(zipEntry)
			zipOs.write(entry.getBytes("UTF-8"))
		}
		finally {
			zipOs.close()
			os.close()
		}		
	}
	
	private void setupCache() {
		
		def cacheDirPath = getCacheDirectory() 
		
		//create cache directory if it doesn't exist
		mkdirAll(cacheDirPath)
		
		//sanity check
		def cacheDir = new File(cacheDirPath)
		if (cacheDir.exists() == false) {
			throw new FileNotFoundException('Cache directory doesn\'t exist: ' +
				cacheDirPath)
		}
	}
	
	private final String cacheDirectory;
	private String getCacheDirectory() {
		return cacheDirectory
	}
	
	private String getEntryPath(String entryName) {
		return getCacheDirectory() + File.separator + entryName + '.zip'
	}		
	
	private void mkdirAll(String path) {
		
		def pathParts = path.
			replaceFirst('^' + File.separator , '').
			replaceFirst(File.separator + '$', '').
			split(File.separator)
		
		def currentPath = File.separator
		pathParts.each { pathPt ->
			currentPath += pathPt + File.separator
			def f = new File(currentPath)
			if (!f.exists()) f.mkdir()
		}
	}
}
