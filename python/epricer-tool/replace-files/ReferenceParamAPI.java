package com.ibm.services.epricer.param;

import java.util.HashMap;

import com.ibm.services.epricer.framework.exceptions.ServiceException;
import com.ibm.services.epricer.framework.rest.DataBean;

public interface ReferenceParamAPI {
	
	public void write(String contentType, String id, String collection, DataBean data) throws ServiceException;
	// Multiple Write and update operations on solr are now Up and running.
	//IN multiple update you need to pass the whole solr object with its "ID".
	public void write(String connectionUrl, String contentType, String id, String collection, DataBean data) throws ServiceException;
	
	public DataBean readOne(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException;
	
	public DataBean read(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException;
	
	public DataBean readSelectedFields(String collectionName, String docName, String queryId, HashMap<String, String> paramMap, String selectedFields) throws ServiceException;
	
	public void delete(String collectionName, String docName, String queryId, HashMap<String, String> paramMap) throws ServiceException;

}
