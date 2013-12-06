package com.carplots.service.documentStore;

public interface DocumentStore<DOC_T, DOC_ID_T> {
	
	DOC_T createDocument(DOC_T document) throws DocumentStoreException;
	DOC_T updateDocument(DOC_T document) throws DocumentStoreException;		
	
	DOC_T getDocument(DOC_ID_T documentId) throws DocumentStoreException; 
	void deleteDocument(DOC_T document) throws DocumentStoreException, DocumentFormatException;
		
	public static class DocumentStoreException extends Exception {
		public DocumentStoreException(final String message) {
			super(message);
		}
	}	
	public static class DocumentFormatException extends Exception {
		public DocumentFormatException(final String message) {
			super(message);
		}
	}
}
