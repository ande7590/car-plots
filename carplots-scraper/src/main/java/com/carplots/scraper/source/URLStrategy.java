package com.carplots.scraper.source;

import java.net.URL;

public interface URLStrategy {
	URL getURL(Object urlData);
}
