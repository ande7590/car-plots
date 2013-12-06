package com.carplots.service.documentStore
import com.carplots.service.documentStore.DocumentStore;
import com.google.inject.Inject;

import groovy.json.JsonException;
import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseException;
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import com.carplots.service.documentStore.DocumentStore.DocumentFormatException;
import com.carplots.service.documentStore.DocumentStore.DocumentStoreException

/**
 * Not intended to work with data directly, this a conduit. In the eyes of this implementation,
 * the actual data format (JSON) is opaque.  We don't care about the data format since
 * this class will be used by external data sources (i.e. non-Java, e.g. R language interop).
 * Treating everything as Strings avoids JSON serialization/deserialization overhead, which
 * is fine since we don't need to examine or manipulate the actual data.
 */
public class DocumentStoreCouchDBStringImpl implements DocumentStore<String, String> {
	
	private final RESTClient restClient
	private final JsonSlurper jsonSlurper = new JsonSlurper()
	
	public DocumentStoreCouchDBStringImpl(final String databaseURL) {
		this.restClient = new RESTClient((databaseURL =~ /\/$/).replaceAll(''))
	}

	@Override
	public String createDocument(String document) {
		try {
			return getRESTClient().post(contentType: JSON,
				body: document).data
		} catch (HttpResponseException ex) {
			throw new DocumentStoreException(ex.response.data)
		}
	}

	@Override
	public String updateDocument(String document) {
		try {
			return getRESTClient().put(contentType: JSON,
				body: document).data;
		} catch (HttpResponseException ex) {
			throw new DocumentStoreException(ex.response.data)
		}
	}

	@Override
	public void deleteDocument(String document) {
		try {
			def doc = jsonSlurper.parseText(document)
			if (!doc.containsKey("_id")) {
				throw new DocumentFormatException("document missing _id field")
			} else if (!doc.containsKey("_rev")) {
				throw new DocumentFormatException("document missing _rev field")
			}
			getRESTClient().request(DELETE, JSON) { req ->
				uri.path += "/${doc._id}"
				uri.addQueryParam('rev', doc._rev)
			}
		} catch (JsonException ex) {
			throw new DocumentFormatException(ex.message)
		}
		catch (HttpResponseException ex) {
			throw new DocumentStoreException(ex.response.responseData)
		}
	}
	
	
	@Override
	public String getDocument(String documentId) throws DocumentStoreException {
		try {
			def response = getRESTClient().request(GET, JSON) { req ->
				uri.path += "/${documentId}"
			}
			return response.responseData
		}
		catch (HttpResponseException ex) {
			throw new DocumentStoreException(ex.response.responseData)
		}
	}

	private RESTClient getRESTClient() {
		return this.restClient
	}
	
	private JsonSlurper getJsonSlurper() {
		return this.jsonSlurper
	}

}
