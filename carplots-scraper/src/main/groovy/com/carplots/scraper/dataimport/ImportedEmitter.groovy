package com.carplots.scraper.dataimport

import com.carplots.persistence.scraper.entities.Imported;

interface ImportedEmitter {
	void emit(Imported imported)
}
